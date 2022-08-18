package cn.edu.scut.ppps;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tokens for the cloud storage.
 * @author Cui Yuxin
 */
public class Tokens {

    private Map<String, Map<String, String>> tokens;
    private Context context;

    /**
     * Constructor.
     * @param context Context of the application.
     * @author Cui Yuxin
     */
    public Tokens(Context context) throws IOException, ClassNotFoundException {
        this.context = context;
        this.tokens = new java.util.HashMap<>();
        File tokensFile = this.loadTokenFile();
        if (tokensFile.exists()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(tokensFile));
            this.tokens = (HashMap<String, Map<String, String>>) objectInputStream.readObject();
        } else {
            // Create a new file
            tokensFile.createNewFile();
            saveTokens();
        }
    }

    /**
     * Load the tokens file and return.
     * @author Cui Yuxin
     */
    private File loadTokenFile() {
        String tokensPath = context.getDataDir().getAbsolutePath() + File.separator + tokens;
        return new File(tokensPath);
    }

    /**
     * Save the tokens to the file.
     * @author Cui Yuxin
     */
    private void saveTokens() throws IOException {
        FileOutputStream outStream = new FileOutputStream(this.loadTokenFile());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
        objectOutputStream.writeObject(this.tokens);
        objectOutputStream.close();
    }

    /**
     * Get a token of a cloud service.
     * @param name Name of the token.
     * @return The token.
     * @author Cui Yuxin
     */
    public Map<String, String> getToken(String name) {
        return tokens.get(name);
    }

    /**
     * Get all the token names and return.
     * @author Cui Yuxin
     */
    public Set<String> getNames() {
        return tokens.keySet();
    }

    /**
     * Update a token of a cloud service.
     * @param name Name of the token.
     * @param token The token.
     * @author Cui Yuxin
     */
    public void updateToken(String name, Map<String, String> token) throws IOException {
        tokens.put(name, token);
        this.saveTokens();
    }
}
