package my_work;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class XepLichThiOrTools {
	
private int N, M, numOfConflict, c[], d[], I1[], I2[]; // cac du lieu muon lay

	static {
		System.loadLibrary("jniortools");
	}
	
	public void readFile() throws FileNotFoundException {
		//Doan nay co the sua de lay duoc file phu hop: 
		// Co the dung dong file de doc lien tiep cac file de thu (ls > names_file)
		String rootpath = "/media/quyentran/A23622BE36229379/A.School/"
    			+ "Toi_uu_lap_KH/Code_/Or-tools/examples/java/my_work/data/";
    	String filename = "3class-4room-2conflict.txt";
    	System.out.println("File name: " + filename); 
    	
    	File file = new File(rootpath + filename);
    	Scanner scanner = new Scanner(file);
    	
    	N = scanner.nextInt();
    	M = scanner.nextInt();
    	numOfConflict =scanner.nextInt();
    	
    	c = new int[M];
    	d = new int[N];
    	I1 = new int[numOfConflict];
    	I2 = new int[numOfConflict];
    	
    	for(int i = 0; i<N; i++) {
    		d[i] = scanner.nextInt();
    	}
    	
    	for(int j = 0; j<M; j++) {
    		c[j] = scanner.nextInt();
    	}
    	
    	for(int i = 0; i<numOfConflict; i++) {
    		I1[i] = scanner.nextInt();
    	}
    	
    	for(int i = 0; i<numOfConflict; i++) {
    		I2[i] = scanner.nextInt();
    	}
    	
	}
	
	public void solve() {
		int MAX = 1000000000;
		
		MPSolver solver = new MPSolver("XepLichThi", 
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		
		//Cac bien:
		
		//X[i][j][k] = 1 --> Mon i thi phong j o kip k
		MPVariable[][][] X = new MPVariable[N+1][M+1][N+1];
		for(int i = 1; i<=N;  i++) {
			for (int j = 1; j<=M; j++) {
				for(int k = 1; k<=N; k++)
					X[i][j][k] = solver.makeIntVar(0, 1, "X[" + i + "," + j +  "," + k +"]");		
			}
		}
		
		//Y[i] = kip thi cua mon i:
		MPVariable[] Y = new MPVariable[N+1];
		for(int i = 1; i<=N; i++) {
			Y[i] = solver.makeIntVar(1, N, "Y[" + i + "]");
		}
		
		
		MPVariable y = solver.makeIntVar(1, N, "y");// y la bien ham muc tieu
		
		
		
		//Cac rang buoc:
		
		//Rang buoc suc chua:
		for(int i = 1; i<=N; i++) {
			MPConstraint c1 = solver.makeConstraint(d[i-1], MAX);
			for(int k = 1; k<=N; k++) {
				for(int j = 1; j<=M; j++) {
					c1.setCoefficient(X[i][j][k], c[j-1]);
				}
			}
		}
		
		//Rang buoc trung thoi gian:
		for(int kk = 0; kk<numOfConflict; kk++) {
			int i1 = I1[kk];
			int i2 = I2[kk];
			
			for(int k = 1; k<=N; k++) {
				MPConstraint c3 = solver.makeConstraint(-MAX, 1);
				for(int j = 1; j<=M; j++) {
					c3.setCoefficient(X[i1][j][k], 1);
					c3.setCoefficient(X[i2][j][k], 1);
				}
			}
		}
		
		//Rang buoc moi mon trong 1 kip chi thi 1 phong trong 1 kip nao do.
		for(int i = 1; i<=N; i++) {
			MPConstraint c4 = solver.makeConstraint(1, 1);
			for(int k = 1; k<=N; k++) {	
				for(int j = 1; j<=M; j++) {
					c4.setCoefficient(X[i][j][k], 1);
				}
			}	
		}
		
		//Tai 1 phong, 1 kip chi thi nhieu nhat 1 mon:
		for(int j = 1; j<=M; j++) {
			for(int k = 1; k<=N; k++) {
				MPConstraint c5 = solver.makeConstraint(1, 1);
				for(int i = 1; i<=N; i++) {
					c5.setCoefficient(X[i][j][k], 1);
				}
			}
		}
		
		//Rang buoc giua X va Y:
		for(int i = 1; i<=N; i++) {
			for(int k = 1; k<=N; k++) {
				MPConstraint c6 = solver.makeConstraint(k+1, k+1);
				for(int j = 1; j<=N; j++) {
					c6.setCoefficient(X[i][j][k], 1);
				}
				c6.setCoefficient(Y[i], 1);
			}
		}
		
		//Rang buoc gia tri kip thi lon nhat:
		MPConstraint c5 = solver.makeConstraint(0, MAX);
		c5.setCoefficient(y, 1);
		for(int i = 1; i<=N; i++) {
			c5.setCoefficient(Y[i], -1);
		}
		
		//Ham muc tieu
		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		
		MPSolver.ResultStatus rs = solver.solve();
		if(rs != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("Cannot find optimal solution!");
		}else {
			System.out.println("obj = " + obj.value());
			for(int i = 01; i<=N; i++) {
				for(int k = 1; k<=N; k++)
				for(int j = 01; j<=N; j++) {
					if(X[i][j][k].solutionValue() == 1) {
						System.out.print("Mon " + i + " thi phong " + j);
						System.out.println(" kip thu" + k);
						
					}
				}
				
			}

		}
		
	}
	
	public static void main(String[] args) {
		XepLichThiOrTools run = new XepLichThiOrTools();
		try {
			run.readFile();
			run.solve();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
