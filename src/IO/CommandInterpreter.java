package IO;

import Judge.Tester;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import network.DownloadManager;
import Repository.StudentsRepository;
import StaticData.SessionData;
import StaticData.ExceptionMessages;



import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CommandInterpreter {

    private Tester tester;
    private StudentsRepository repository;
    private DownloadManager downloadManager;
    private IOManager inputOutputManager;

    public CommandInterpreter(Tester tester, StudentsRepository repository, DownloadManager downloadManager, IOManager inputOutputManager) {
        this.tester = tester;
        this.repository = repository;
        this.downloadManager = downloadManager;
        this.inputOutputManager = inputOutputManager;
    }

    public void interpretCommand(String input) throws IOException {
        String[] data = input.split("\\s+");
        String command = data[0];
        try {
            parseCommand(input, data, command);
        } catch (IllegalArgumentException iae) {
            OutputWriter.displayException(iae.getMessage());
        } catch (StringIndexOutOfBoundsException sioobe) {
            OutputWriter.displayException(sioobe.getMessage());
        } catch (IOException ioe) {
            OutputWriter.displayException(ioe.getMessage());
        } catch (Throwable t) {
            OutputWriter.displayException(t.getMessage());
        }
    }

    private void parseCommand(String input, String[] data, String command) throws Exception {
        switch (command) {
            case "open":
                tryOpenFile(command, data);
                break;
            case "mkdir":
                tryCreateDirectory(command, data);
                break;
            case "ls":
                tryTraverseFolders(command, data);
                break;
            case "cmp":
                tryCompareFiles(command, data);
                break;
            case "cdrel":
                tryChangeRelativePath(input, data);
                break;
            case "changeDirRel":
                tryChangeRelativePath(command, data);
                break;
            case "changeDirAbs":
                tryChangeAbsolutePath(command, data);
                break;
            case "readDb":
                tryReadDatabaseFromFile(command, data);
                break;
            case "show":
                tryShowWantedCourse(command, data);
                break;
            case "filter":
                tryPrintFilteredStudents(command, data);
                break;
            case "order":
                tryPrintOrderedStudents(command, data);
                break;
            case "download":
                tryDownloadFile(command, data);
                break;
            case "downloadAsynch":
                tryDownloadFileAsynchronously(command, data);
                break;
            case "dropdb":
                tryDropDb(command, data);
                break;
            case "help":
                tryHelp();
                break;
            default:
                displayInvalidCommandMessage(command);
                break;
        }
    }

    private void tryDropDb(String command, String[] data) {
        if (data.length != 1) {
            this.displayInvalidCommandMessage(command);
            return;
        }

        this.repository.unloadData();
        OutputWriter.writeMessageOnNewLine("Database dropped!");
    }

    private void tryDownloadFile(String input, String[] data) {
        if (data.length != 2) {
            displayInvalidCommandMessage(input);
            return;
        }
    }



    private void displayInvalidCommandMessage(String command){
        String output = String.format("The command '%s' is invalid", command);
        OutputWriter.writeMessageOnNewLine(output);
    }

    private void tryOpenFile(String command, String[] data) throws IOException {
        if (data.length != 2){
            displayInvalidCommandMessage(command);
            return;
        }

        String fileName = data[1];
        String filePath = SessionData.currentPath + "\\" + fileName;
        File file = new File(filePath);
        Desktop.getDesktop().open(file);
    }

    private void tryCreateDirectory(String command, String[] data) {
        if (data.length != 2){
            System.out.println("Data: " + data);
            displayInvalidCommandMessage(command);
            return;
        }

        String folderName = data[1];
        this.inputOutputManager.createDirectoryInCurrentFolder(folderName);
    }

    private void tryTraverseFolders(String command, String[] data) {
        if (data.length != 1 && data.length != 2){
            displayInvalidCommandMessage(command);
            return;
        }

        if (data.length == 1){
            this.inputOutputManager.traverseDirectory(0);
        }
        if (data.length == 2){
            this.inputOutputManager.traverseDirectory(Integer.parseInt(data[1]));
        }
    }

    private void tryCompareFiles(String command, String[] data) throws Exception {
        if (data.length != 3){
            displayInvalidCommandMessage(command);
            return;
        }

        String firstPath = data[1];
        String secondPath = data[2];
        this.tester.compareContent(firstPath, secondPath);
    }

    private void tryChangeRelativePath(String command, String[] data) throws IOException {
        if (data.length != 2){
            displayInvalidCommandMessage(command);
            return;
        }

        String relativePath = data[1];
        this.inputOutputManager.changeCurrentDirRelativePath(relativePath);
    }

    private void tryChangeAbsolutePath(String command, String[] data) throws IOException {
        if (data.length != 2){
            displayInvalidCommandMessage(command);
            return;
        }

        String absolutePath = data[1];
        this.inputOutputManager.changeCurrentDirAbsolutePath(absolutePath);
    }

    private void tryReadDatabaseFromFile(String command, String[] data) throws IOException {
        if (data.length != 2){
            displayInvalidCommandMessage(command);
            return;
        }

        String fileName = data[1];
        this.repository.loadData(fileName);
    }

    private void tryShowWantedCourse(String command, String[] data) {
        if (data.length != 2 && data.length != 3) {
            displayInvalidCommandMessage(command);
            return;
        }

        if (data.length == 2) {
            String courseName = data[1];
            this.repository.getStudentsByCourse(courseName);
        }

        if (data.length == 3) {
            String courseName = data[1];
            String userName = data[2];
            this.repository.getStudentMarksInCourse(courseName, userName);
        }
    }

    private void tryHelp() {
        OutputWriter.writeMessageOnNewLine("mkdir path - make directory");
        OutputWriter.writeMessageOnNewLine("ls depth - traverse directory");
        OutputWriter.writeMessageOnNewLine("cmp path1 path2 - compare two files");
        OutputWriter.writeMessageOnNewLine("changeDirRel relativePath - change directory");
        OutputWriter.writeMessageOnNewLine("changeDir absolutePath - change directory");
        OutputWriter.writeMessageOnNewLine("readDb path - read students data base");
        OutputWriter.writeMessageOnNewLine("filterExcelent - filter excelent students (the output is written on the console)");
        OutputWriter.writeMessageOnNewLine("filterExcelent path - filter excelent students (the output is written in a given path)");
        OutputWriter.writeMessageOnNewLine("filterAverage - filter average students (the output is written on the console)");
        OutputWriter.writeMessageOnNewLine("filterAverage path - filter average students (the output is written in a file)");
        OutputWriter.writeMessageOnNewLine("filterPoor - filter low grade students (the output is on the console)");
        OutputWriter.writeMessageOnNewLine("filterPoor path - filter low grade students (the output is written in a file)");
        OutputWriter.writeMessageOnNewLine("order - sort students in increasing order (the output is written on the console)");
        OutputWriter.writeMessageOnNewLine("order path - sort students in increasing order (the output is written in a given path)");
        OutputWriter.writeMessageOnNewLine("decOrder - sort students in decreasing order (the output is written on the console)");
        OutputWriter.writeMessageOnNewLine("decOrder path - sort students in decreasing order (the output is written in a given path)");
        OutputWriter.writeMessageOnNewLine("download pathOfFile - download file (saved in current directory)");
        OutputWriter.writeMessageOnNewLine("downloadAsync path - download file asynchronously (save in the current directory)");
        OutputWriter.writeMessageOnNewLine("help - get help");
        OutputWriter.writeEmptyLine();
    }

    private void tryPrintFilteredStudents(String command, String[] data) {
        if (data.length != 3 && data.length != 4) {
            displayInvalidCommandMessage(command);
            return;
        }

        String course = data[1];
        String filter = data[2];

        if (data.length == 3){
//            this.repository.printFilteredStudents(course, filter, null);
            return;
        }

        Integer numberOfStudents = Integer.valueOf(data[3]);
        if (data.length == 4){
//            this.repository.printFilteredStudents(course, filter, numberOfStudents);
            return;
        }
    }

    private void tryPrintOrderedStudents(String command, String[] data) {
        if (data.length != 3 && data.length != 4) {
            displayInvalidCommandMessage(command);
            return;
        }

        String course = data[1];
        String compareType = data[2];

        if (data.length == 3){
//            this.repository.printOrderedStudents(course, compareType, null);
            return;
        }

        Integer numberOfStudents = Integer.valueOf(data[3]);
        if (data.length == 4){
//            this.repository.printOrderedStudents(course, compareType, numberOfStudents);
            return;
        }
    }



    private void tryDownloadFileAsynchronously(String command, String[] data) {
    }
}
