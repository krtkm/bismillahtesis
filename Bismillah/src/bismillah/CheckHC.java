package bismillah;

/**
 *
 * @author user
 */
public class CheckHC {
    
    boolean hardConstraint(int[][] schStudent, int[][] suitableroom, int[][] suitableSlot, int[][] suitableOrder, 
            int[] solRoom, int[] solTimeslot) 
    {
        if (HC1(schStudent) || HC2(suitableroom, solRoom) || HC3(solTimeslot, solRoom) 
                || HC4(solTimeslot, suitableSlot) 
                || HC5(solTimeslot, suitableOrder)
                ) {
            return true;
        }
        return false;
    }
    
    /*
    Hard Constraint 1: 
    Tidak ada mahasiswa yang mengikuti lebih dari satu kelas dalam satu waktu
    */
    boolean HC1 (int[][] schStudent) {
        for (int j = 0; j < schStudent.length; j++) {
            for (int k = 0; k < schStudent[j].length; k++) {
                if (schStudent[j][k] > 1) {
//                    System.out.println("TIDAK lolos HC 1");
                    return false;
                }
            }
        } 
        return true;
    }
    
    /*
    Hard Constraint 2:
    Ruangan harus memenuhi semua features yang dibutuhkan oleh mata kuliah, 
    termasuk mencukupi untuk jumlah mahasiswa yang mengambil kelas mata kuliah 
    di ruangan tersebut.
    */
    boolean HC2(int[][] suitableroom, int[] solRoom) {
        for (int i = 0; i < suitableroom.length; i++) {
            int rooms = solRoom[i];
            if (suitableroom[i][rooms-1] < 1) {
//                System.out.println("TIDAK lolos HC 2");
                return false;
            }
        }
       return true;
    }
    
    /*
    Hard Constraint 3:
    Tidak ada lebih dari satu mata kuliah yang diselenggarakan 
    dalam satu ruangan dan dalam satu waktu.
    */
    boolean HC3(int[] solTimeslot, int[] solRoom) {
        for (int i = 0; i < solTimeslot.length; i++) {
            int slot = solTimeslot[i];
            int room = solRoom[i];
            for (int j = i+1; j < solTimeslot.length; j++) {
                if (solTimeslot[j]==slot && solRoom[j]==room) {
//                    System.out.println("TIDAK lolos HC 3");
                    return false;
                }
            }
        }
        return true;
    }
    
    /* ITC-2007 Datasets
    Hard Constraint 4:
    Mata kuliah hanya ditempatkan ke slot waktu yang telah ditentukan sebelumnya
    */
    boolean HC4(int[] solTimeSlot, int[][] suitableSlot) {
        for (int i = 0; i < solTimeSlot.length; i++) {
            for (int j = i+1; j < solTimeSlot.length; j++) {
                int solSlot = solTimeSlot[j];
                if (suitableSlot[j][solSlot]==0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /*
    Hard Constraint 5:
    bila ditentukan, event dijadwalkan dalam urutan yang telah ditetapkan dalam seminggu
    */
    boolean HC5(int[] solTimeSlot, int[][] suitableOrder) {
        for (int i = 0; i < solTimeSlot.length; i++) {
            int eventA=solTimeSlot[i];
            for (int j = i+1; j < solTimeSlot.length; j++) {
                int eventB = solTimeSlot[j];
                if (eventA > eventB && suitableOrder[i][j]<0) {
                    return false;
                } // ITC-2007 jika matrix urutan order 
                if (eventA < eventB && suitableOrder[i][j]>0) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
