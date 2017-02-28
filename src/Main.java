import IO.CommandInterpreter;
import IO.IOManager;
import IO.InputReader;
import IO.OutputWriter;
import Judge.Tester;
import Repository.RepositoryFilter;
import Repository.RepositorySorter;
import Repository.StudentsRepository;
import network.DownloadManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Tester tester = new Tester();
        RepositoryFilter filter = new RepositoryFilter();
        RepositorySorter sorter = new RepositorySorter();
        StudentsRepository repository = new StudentsRepository(filter, sorter);
        DownloadManager downloadManager = new DownloadManager();
        IOManager ioManager = new IOManager();
        CommandInterpreter currentInterpreter = new CommandInterpreter(tester, repository, downloadManager, ioManager);
        InputReader reader = new InputReader(currentInterpreter);

        try {
            reader.readCommands();
        } catch (Exception e) {
            OutputWriter.displayException(e.getMessage());
        }
    }
}
