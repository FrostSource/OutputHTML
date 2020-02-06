import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.regex.Matcher;


public class Main {

    public static String getInput(String message) {
        // Creates a scanner based on System.in and returns the next line
        Scanner scan = new Scanner(System.in);
        System.out.print(message + ": ");
        String input = scan.nextLine();
        // closing the scanner also closes System.in, stopping any future input
        //scan.close();
        return input;
    }

    public static String getFileContent(String path) throws IOException {
        // Returns all the text content in a file
        // Preserving newlines
        /*String content = "";
        Scanner reader = new Scanner(path);
        while (reader.hasNextLine()) {
            content += reader.nextLine();
            content += "\n";
        }
        reader.close();
        return content;*/

        String content = "";
        String line;
        try {
            InputStream is = Main.class.getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                content += line;
                content += "\n";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }

    public static boolean cleanDirectory(File dir) {
        // Deletes all files and subdirectories in a directory
        if (!dir.exists()) return false;

        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                cleanDirectory(file);

            file.delete();
        }
        return true;
    }

    public static void main(String[] args) throws IOException {

        String HTMLTemplate;

        // Importing the HTML index template to edit
        try {
            HTMLTemplate = Main.getFileContent("templates/html.txt");
        } catch (NoSuchFileException e) {
            System.out.println(Paths.get("templates/html.txt").toAbsolutePath());
            System.out.println("Template \"templates/html.txt\" does not exist.");
            System.out.println("\nPress enter to exit."); System.in.read();
            return;
        }

        // Greeting message
        System.out.println("HTML Generator!");

        // Get the title
        String title = Main.getInput("Title");
        HTMLTemplate = HTMLTemplate.replaceAll("\\{TITLE\\}", Matcher.quoteReplacement(title));

        // Get the body text
        String text = Main.getInput("Text");
        HTMLTemplate = HTMLTemplate.replaceAll("\\{BODY_TEXT}", Matcher.quoteReplacement(text));

        // Get image file and check if it exists
        String image = Main.getInput("Image");
        File imageFile = new File(image);
        if (!imageFile.exists()) {
            System.out.println("File " + image + " does not exist.");
            System.out.println("\nPress enter to exit."); System.in.read();
            return;
        } else if (imageFile.isDirectory()) {
            System.out.println("Must be an image file, not a directory.");
            System.out.println("\nPress enter to exit."); System.in.read();
            return;
        }
        HTMLTemplate = HTMLTemplate.replaceAll("\\{IMAGE}", Matcher.quoteReplacement(imageFile.getName()));

        // Get the body colour
        String colour = Main.getInput("Colour");
        HTMLTemplate = HTMLTemplate.replaceAll("\\{BGCOLOUR}", Matcher.quoteReplacement(colour));

        // Create the path with title as name
        // Replacing all non-word character to make it safe
        Path directory = Paths.get(title.replaceAll("[^\\w \\-]", ""));

        // If the directory already exists then delete all files inside
        // to get rid of unused image files
        if (Files.exists(directory)) {

            // Warn the user that the contents will be erased and get response
            String response = Main.getInput("This will erase the contents of \"" + directory.toAbsolutePath() + "\" are you sure? (y/n)");

            if (response.equalsIgnoreCase("y")) {
                // Delete all files in directory
                Main.cleanDirectory(directory.toFile());

            } else {
                // Exit program if user chooses to not erase
                System.out.println("You have chosen not to erase contents.");
                System.out.println("\nPress enter to exit."); System.in.read();
                return;
            }

        } else {
            Files.createDirectories(directory);
        }

        // Copy over the image file into the directory
        Files.copy(imageFile.toPath(), (new File(directory.toString() + "/" + imageFile.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Create the index html file with the new data
        File index = new File(directory.toString() + "/index.html");
        Files.writeString(index.toPath(), HTMLTemplate);

        // Display location of new files
        System.out.println("Your files have been output to \"" + directory.toAbsolutePath() + "\"");

    }

}
