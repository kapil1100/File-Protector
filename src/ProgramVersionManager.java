import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class ProgramVersionManager {
    private final String programVersion = "File Protector v3.2";
    private final String versionInfoFileName = "versionInfo.inf";
    private File versionFileLoc;

    //adding a version info file in encrypted folder.
    public void addVersionInfoFile(File rootFolderLoc) {
        File infoFile = new File(rootFolderLoc + "\\" + versionInfoFileName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(infoFile));
            writer.write("This folder is encytped using : ");
            writer.newLine();
            writer.write(programVersion);
            writer.newLine();
            writer.newLine();
            writer.write("Created by - Kapil Bansal");
            writer.newLine();
            writer.newLine();
            writer.write("********* Do NOT remove this versionInfo file *********");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //setting file to read only mode so that user can't edit it.
        infoFile.setReadOnly();
    }

    //checks whether the version file present in rootFolderLoc or not.
    public boolean isVersionFilePresent(File rootFolderLoc) {
        versionFileLoc = new File(rootFolderLoc + "\\" + versionInfoFileName);
        return versionFileLoc.exists();
    }

    //checks whether the folder is encrypted using the same version of file protector or not.
    public boolean isEncryptedWithSameProgramVersion() {
        boolean isSameVersion = false;
        try {
            Scanner scanner = new Scanner(new FileReader(versionFileLoc));
            String versionInfoInFile = scanner.nextLine();
            versionInfoInFile = scanner.nextLine();
            //if the program base version in same i.e. v2.1 = v2.2
            if (versionInfoInFile.charAt(16) == programVersion.charAt(16))
                isSameVersion = true;
            else
                //folder is encrypted using another version of File Protector.
                isSameVersion = false;

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSameVersion;
    }

    //displays the version of file protector with which the folder is encrypted
    public void folderIsEncryptedWithProgramVersion() {
        try {
            Scanner scanner = new Scanner(new FileReader(versionFileLoc));
            String versionInfoInFile = scanner.nextLine();
            versionInfoInFile = scanner.nextLine();
            JOptionPane.showMessageDialog(null,
                    "This folder is encrypted using " + versionInfoInFile +
                            "\nand you are using: " + programVersion,
                    "Version Info: ", JOptionPane.OK_OPTION);
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProgramVersion() {
        return programVersion;
    }
}
