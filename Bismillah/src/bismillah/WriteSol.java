package bismillah;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class WriteSol {
    WriteSol(String nameFile, int[] courseTimeslot, int[] courseRoom) {
        BufferedWriter bw = null;
        try {
            String result = String.valueOf(courseTimeslot.length);
            for (int i = 0; i < courseTimeslot.length; i++) {
                result = result + "\n" + courseTimeslot[i] 
                        + "," + courseRoom[i];
            }
            File file = new File(nameFile);
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(result);
        } catch (IOException ioe) {
	} finally {
            try{
                if(bw!=null) bw.close();
            } catch(IOException ex){
                System.out.println("Error in closing the BufferedWriter"+ex);
            }
        }
    }
}
