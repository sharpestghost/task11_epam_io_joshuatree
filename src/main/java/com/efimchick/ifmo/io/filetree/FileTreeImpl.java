package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileTreeImpl implements FileTree {

    private static final String NEW_DIRECTORY_SYMBOL = "├─ ";
    private static final String NEW_FILE_IN_DIRECTORY_SYMBOL = "│  ";
    private static final String LAST_FILE_IN_DIRECTORY = "└─ ";
    private static final String BLANK_SPACE = "   ";
    private static final String WORD_DELIMITIER = " ";
    private static final String SIZE_MEASURE = " bytes";

    @Override
    public Optional<String> tree(Path path) {
        File file = new File(String.valueOf(path));
        Optional<String> result = Optional.empty();
        if (file.isFile()) {
            result = Optional.of(printFileData(file));
        }
        if (file.isDirectory()) {
            result = Optional.of(printFileTree(file, new ArrayList<>()));
        }
        return result;
    }

    private String printFileTree(File currentFolder, List<Boolean> isParentFolderLastInDirectoryList) {
        StringBuilder directory = new StringBuilder();
        if (!isParentFolderLastInDirectoryList.isEmpty()) {
            directory.append(addNotRootFilePathInformation(isParentFolderLastInDirectoryList.
                    get(isParentFolderLastInDirectoryList.size() - 1)));
        }
        directory.append(printFolderData(currentFolder));
        List<File> files = sortFiles(currentFolder.listFiles());
        int count = files.size();
        for (int i = 0; i < count; i++) {
            boolean isFileLastInDirectory = count == i + 1;
            directory.append(addChildFileInformation(files.get(i),
                    isFileLastInDirectory, isParentFolderLastInDirectoryList));
        }
        return directory.toString();
    }

    private String addNotRootFilePathInformation(boolean isFileLastInDirectory) {
        String pathInformation;
        if (isFileLastInDirectory) {
            pathInformation = LAST_FILE_IN_DIRECTORY;
        } else {
            pathInformation = NEW_DIRECTORY_SYMBOL;
        }
        return pathInformation;
    }

    private String addChildFileInformation (File file, boolean isFileLastInDirectory,
                                            List<Boolean> isParentFolderLastInDirectoryList) {
        StringBuilder directory = new StringBuilder();
        directory.append(addFilePathInformation(isParentFolderLastInDirectoryList));
        if (file.isFile()) {
            directory.append(addNotRootFilePathInformation(isFileLastInDirectory));
            directory.append(printFileData(file));
        } else {
            isParentFolderLastInDirectoryList.add(isFileLastInDirectory);
            directory.append(printFileTree(file, isParentFolderLastInDirectoryList));
            isParentFolderLastInDirectoryList.remove(isParentFolderLastInDirectoryList.size() - 1);
        }
        return directory.toString();
    }

    private long getFolderSize(File folder) {
        long folderSize = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    folderSize += file.length();
                } else {
                    folderSize += getFolderSize(file);
                }
            }
        }
        return folderSize;
    }

    private String printFolderData(File currentFolder) {
        return currentFolder.getName() + WORD_DELIMITIER + getFolderSize(currentFolder) + SIZE_MEASURE;
    }

    private String printFileData(File file) {
        return file.getName() + WORD_DELIMITIER + file.length() + SIZE_MEASURE;
    }

    private String addFilePathInformation(List<Boolean> isParentFolderLastInDirectoryList) {
        StringBuilder directoryPath = new StringBuilder("\n");
        for (boolean isParentFolderLast : isParentFolderLastInDirectoryList) {
            if (isParentFolderLast) {
                directoryPath.append(BLANK_SPACE);
            } else {
                directoryPath.append(NEW_FILE_IN_DIRECTORY_SYMBOL);
            }
        }
        return directoryPath.toString();
    }

    private List<File> sortFiles(File[] folder) {
        Comparator<File> directoryComparator = Comparator.comparing((File file) -> !file.isDirectory()).
                thenComparing((File file) -> file.toString().toUpperCase(Locale.CANADA));
        Arrays.sort(folder, directoryComparator);
        return Arrays.stream(folder).collect(Collectors.toList());
    }
}
