package bismillah;

/**
 *
 * @author AM
 */
public class CheckPenalti {
    
    int totalPenalti(int[][] studentavailability){
        return (SC1(studentavailability)+SC2(studentavailability)+SC3(studentavailability));
    }
    
    /*
    Mahasiswa tidak mengambil lebih dari 2 mata kuliah berturut-turut
    dalam satu hari
    */
    int SC1(int[][] studentavailability) {
        int score = 0;
        for (int i = 0; i < studentavailability.length; i++) {
            int numCourse = 0;
            for (int j = 0; j < studentavailability[i].length; j++) {
                if (j==9 || j==18 || j==27 || j==36) {
                    numCourse = 0;
                }
                if (studentavailability[i][j] == 1) {
                    numCourse++;
                } else {
                    numCourse = 0;
                }
                if (numCourse >= 3) {
                    score++;
                }
            }
        }
        return score;
    }
    
    /*
    Mahasiswa tidak mengambil tepat hanya satu mata kuliah saja
    dalam satu hari
    */
    int SC2(int[][] studentavailability) {
        int score = 0;
        for (int i = 0; i < studentavailability.length; i++) {
            int[] numCourse = new int[5];
            for (int j = 0; j < studentavailability[i].length; j++) {
                if (j<9) {
                    if (studentavailability[i][j] == 1) numCourse[0]++;
                } else if (j<18) {
                    if (studentavailability[i][j] == 1) numCourse[1]++;
                } else if (j<27) {
                    if (studentavailability[i][j] == 1) numCourse[2]++;
                } else if (j<36) {
                    if (studentavailability[i][j] == 1) numCourse[3]++;
                } else if (j<45) {
                    if (studentavailability[i][j] == 1) numCourse[4]++;
                }
            }
            if (numCourse[0] == 1) score++;
            if (numCourse[1] == 1) score++;
            if (numCourse[2] == 1) score++;
            if (numCourse[3] == 1) score++;
            if (numCourse[4] == 1) score++;
        }
        return score;
    }
    
    /*
    mahasiswa tidak mengambil mata kuliah di akhir sesi
    */
    int SC3(int[][] studentavailability) {
        int score = 0;
        for (int i = 0; i < studentavailability.length; i++) {
            for (int j = 0; j < studentavailability.length; j++) {
                if (j==8 || j==17 || j==26 || j==35 || j==44) {
                    if (studentavailability[i][j] == 1) {
                        score++;
                    }
                }
                
            }
        }
        return score;
    }
}
