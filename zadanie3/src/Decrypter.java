import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

class Decrypter implements DecrypterInterface {

    String txt;
    HashMap<Character, Character> decode = new HashMap<>();

    @Override
    public void setInputText(String encryptedDocument) {

        decode.clear();

        if (encryptedDocument != null) {
            txt = encryptedDocument;
            String[] words = txt.split("\\s+");
            char[] msg = {'W', 'y', 'd', 'z', 'i', 'a', 'ł', 'F', 'i', 'z', 'y', 'k', 'i', ',', 'A', 's', 't', 'r', 'o', 'n', 'o', 'm', 'i', 'i', 'i', 'I', 'n', 'f', 'o', 'r', 'm', 'a', 't', 'y', 'k', 'i', 'S', 't', 'o', 's', 'o', 'w', 'a', 'n', 'e', 'j'};
            String name = "";
            for (int i = 0; i <= words.length - 6; ++i) {
                boolean check = true;
                if (words[i].length() == 7 && words[i + 1].length() == 7 && words[i + 2].length() == 10 && words[i + 3].length() == 1 && words[i + 4].length() == 11 && words[i + 5].length() == 10) {
                    name = name + words[i] + words[i + 1] + words[i + 2] + words[i + 3] + words[i + 4] + words[i + 5];
                } else {
                    name = "";
                    continue;
                }


                for (int j = 0; j < msg.length; ++j) {
                    if (decode.containsKey(name.charAt(j))) {
                        if (decode.get(name.charAt(j)) != msg[j]) {
                            decode.clear();
                            check = false;
                            break;
                        }
                    } else {
                        decode.put(name.charAt(j), msg[j]);
                    }
                }
                Character[] values = new Character[decode.size()];
                decode.values().toArray(values);
                for(int x = 0; x < decode.size(); ++x){
                    for(int y = 0; y < decode.size(); ++y){
                        if(x != y && values[x].charValue() == values[y].charValue()){
                            decode.clear();
                            check = false;
                            break;
                        }
                    }
                }
                if (check) {
                    decode.remove(',');
                    return;
                }
            }
        }
    }


    @Override
    public Map<Character, Character> getCode() {
        HashMap<Character, Character> code = new HashMap<>();
        for (Map.Entry<Character, Character> entry : decode.entrySet()) {
            code.put(entry.getValue(), entry.getKey());
        }
        code.remove(',');
        return code;
    }


    @Override
    public Map<Character, Character> getDecode() {
        return decode;
    }
}
