import java.io.*;
import java.net.Socket;
import java.util.*;

public class PasswordCracker implements PasswordCrackerInterface {

    Map<Character, List<Character>> passwordComponents = new HashMap<>(PasswordComponents.passwordComponents);

    @Override
    public String getPassword(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println(input.readLine());
            output.write("Program\n");
            output.flush();

            System.out.println(input.readLine());
            String passwordPattern = input.readLine();
            System.out.println(passwordPattern);
            System.out.println(input.readLine());
            passwordPattern = passwordPattern.split(" ")[2];

            String password = guessPassword(passwordPattern, input, output);

            input.close();
            output.close();
            socket.close();

            return password;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String guessPassword(String passwordPattern, BufferedReader input, BufferedWriter output) throws IOException {
        String password;
        StringBuilder possible = new StringBuilder(passwordPattern.length());
        Map<Character, List<Character>> comp = new HashMap<>(PasswordComponents.passwordComponents);
        int index = 0;
        int tmp = 0;

        Random random = new Random();

        while (true) {

            if (possible.length() == passwordPattern.length()) {
                password = getServerResponse(possible.toString(), input, output);
                int guessedElements = getNumberOfGuessedElements(password);

                if (guessedElements == 0) {
                    break;
                } else {
                    possible.setLength(0);
                }
            }

            for (int i = 0; i < passwordPattern.length(); ++i){
                Character element = passwordPattern.charAt(i);
                List<Character> components = comp.get(element);
                index = random.nextInt(components.size());
                possible.append(components.get(index));
            }
        }

        index = 0;
        while (true) {
            password = getServerResponse(possible.toString(), input, output);
            int guessedElements;
            if (passwordGuessed(password)) {
                return possible.toString();
            }
            for (Character key : passwordComponents.keySet()) {
                char[] charPattern = passwordPattern.toCharArray();

                if (key == charPattern[index]) {
                    List<Character> components = passwordComponents.get(key);
                    possible.setCharAt(index, components.get(tmp));
                    password = getServerResponse(possible.toString(), input, output);
                    if (passwordGuessed(password)) {
                        return possible.toString();
                    }
                    guessedElements = getNumberOfGuessedElements(password);
                    tmp++;

                    if (guessedElements > index) {
                        index++;
                        tmp = 0;
                    }
                }
            }
        }
    }

    private int getNumberOfGuessedElements(String response) {
        if (response.length() != 3){
            String guessedElements = response.split(" ")[4];
            int result = Integer.parseInt(guessedElements);
            return result;
        } else {
            return 1;
        }
    }

    private String getServerResponse(String possiblePassword, BufferedReader input, BufferedWriter output) throws IOException {
        output.write(possiblePassword + "\n");
        output.flush();
        String response = input.readLine();
        return response;
    }

    private boolean passwordGuessed(String response) {
        return response.equals("+OK");
    }
}
