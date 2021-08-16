import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InitTestFiles {

    public static void main(String[] args) throws IOException {
//        File directory = new File("D:\\directory");
//        directory.mkdir();
//        File subdirectory = new File(directory.getAbsolutePath() + "/subdirectory");
//        subdirectory.mkdir();
//        File subFile = new File(directory.getAbsolutePath() + "/subfile.txt");
//        subFile.createNewFile();
//        File subsubFile = new File(subdirectory.getAbsolutePath() + "/subsubfile.txt");
//        FileWriter fileWriter1 = new FileWriter(subFile);
//        fileWriter1.write("File 1");
//        fileWriter1.close();
//        FileWriter fileWriter2 = new FileWriter(subsubFile);
//        fileWriter2.write("File 2");
//        fileWriter2.close();
        File directory = new File("/home/shahondin1624/directory");
        directory.mkdir();
        File subdirectory = new File(directory.getAbsolutePath() + "/subdirectory");
        subdirectory.mkdir();
        File subFile = new File(directory.getAbsolutePath() + "/subfile.txt");
        subFile.createNewFile();
        File subsubFile = new File(subdirectory.getAbsolutePath() + "/subsubfile.txt");
        FileWriter fileWriter1 = new FileWriter(subFile);
        fileWriter1.write("File 1");
        fileWriter1.close();
        FileWriter fileWriter2 = new FileWriter(subsubFile);
        fileWriter2.write("File 2");
        fileWriter2.close();
    }
}
