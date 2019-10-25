package bismillah;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author user
 */
public class ReadSol {
    
    int eventSol;
    String[] trArray;
    int[] solTimeslot, solRoom;
    
    ReadSol() {
        
    }
    
    ReadSol(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);                                             //read file .sol run via netbeans
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        
//        InputStream in = new FileInputStream("./" + filename);                        //read file .tim run via .jar
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        
        eventSol = Integer.parseInt(bufferedReader.readLine());                      //read n-eventSol
        solTimeslot = new int[eventSol];
        solRoom = new int[eventSol];
        for (int i = 0; i < eventSol; i++) {
            trArray = bufferedReader.readLine().split(",");
            solTimeslot[i] = Integer.parseInt(trArray[0]);
            solRoom[i] = Integer.parseInt(trArray[1]);
        }
    }
    
    //Matrik kesibukan mahasiswa dalam kelas (student-timeslot)
    int[][] studentAvail(int[][] studentevent, int totaltimeslot, int[] solTimeslot) {
        int[][] studentAvailability = new int[studentevent.length][totaltimeslot];
        for (int i = 0; i < studentevent.length; i++) {
            for (int j = 0; j < studentevent[i].length; j++) {
                int slot = solTimeslot[j];
                if (studentevent[i][j] > 0) {
                    studentAvailability[i][slot-1]++;
                }
            }
        }
        return studentAvailability;
    }
}
