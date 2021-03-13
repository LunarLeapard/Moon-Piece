
import java.util.*;

public class ScannerPlus{

	private Scanner console;

	public ScannerPlus(){
		console = new Scanner(System.in);
	}

	public int[][] next2DIntArray(String prompt){
		System.out.print(prompt);
		int[][] darray = new int[0][0];
		int i=0;
		int uInputi = 0;
		String uInputs = "";
		while(!uInputs.equalsIgnoreCase("/e")){
			if (console.hasNextInt()){
				if(darray.length==0){
					darray = Arrays.copyOf(darray, darray.length+1);
					darray[darray.length-1] = new int[0];
				}
				darray[i] = Arrays.copyOf(darray[i], darray[i].length+1);
				darray[i][darray[i].length-1] = console.nextInt();
			} else {
				uInputs = console.next();
				if(uInputs.equalsIgnoreCase("/n")){
					darray = Arrays.copyOf(darray, darray.length+1);
					darray[darray.length-1] = new int[0];
					i += 1;
				}
			}
		}
		return darray;
	}
	public int[] nextIntArr(String prompt, int size){
		System.out.print(prompt);
		int[] nextIntArr = new int[size];
		for(int i=0; i<size; i++){
			nextIntArr[i] = this.nextIntP("");
		}
		return nextIntArr;
	}
	public String nextArrElmnt(String prompt, String[] array){
		System.out.print(prompt);
		String ans = console.next();
		while(!inArr(ans, array)){
			System.out.println("Sorry, Not Recognized. Try again");
			ans = console.next();
		}
		return ans;
	}

	public boolean inArr(String ans, String[] array){
		for(String s: array){
			if(ans.equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}

	public int nextIntP(String prompt){
		System.out.print(prompt);
		while(!console.hasNextInt()){
			System.out.println("Sorry, Not Recognized. Try again.");
			String trash = console.next();
		}
		int ans = console.nextInt();
		return ans;
	}
	public String nextIntOrStrP(String prompt, String[] exceptions){
		System.out.print(prompt);
		while(!console.hasNextInt()){
			System.out.println("Sorry, Not Recognized. Try again.");
			String trash = console.next();
			if(ArrayMthds.hasElmntIgnoreCase(trash, exceptions)){
				return trash;
			}
		}
		int ans = console.nextInt();
		return "" + ans;
	}
	public double nextDoubleP(String prompt){
		System.out.print(prompt);
		while(!console.hasNextDouble()){
			System.out.println("Sorry, Not Recognized. Try again!");
			String trash = console.next();
		}
		double ans = console.nextDouble();
		return ans;
	}
	public String[][] next2DStringArray(String prompt, int length, int width){
		String[][] ans = new String[width][length];
		for(int i=0; i<width; i++){
			for(int j=0; j<length; j++){
				ans[i][j] = console.next();
			}
		}
		return ans;
	}
	public String nextP(String prompt){
		System.out.print(prompt);
		String ans = console.next();
		return ans;
	}
	public String nextLineP(String prompt){
		System.out.print(prompt);
		String ans = console.nextLine();
		return ans;
	}
	public ArrayList<String> nextStrLstLmtd(String prompt, ArrayList<String> cllct){
		System.out.print(prompt);
		cllct.add("/e");
		ArrayList<String> nCllct = new ArrayList<String>();
		String ans = this.nextArrElmnt("", (String[])cllct.toArray());
		while(!ans.equalsIgnoreCase("/e")){
			if(!nCllct.contains(ans)){
				nCllct.add(ans);
			}
			ans = this.nextArrElmnt("", (String[]) cllct.toArray());
		}
		return nCllct;
	}
	public String[] nextStrArrLmtd(String prompt, String[] arr){
		ArrayList<String> nCllct = this.nextStrLstLmtd(prompt, Conversion.strArrToCllct(arr));
		return Conversion.objArrToStrArr(nCllct.toArray());
	}
	public ArrayList<String> nextStrCllct(String prompt, String stop){
		System.out.print(prompt);
		ArrayList<String> ans = new ArrayList<String>();
		String elmnt = console.next();
		while(!elmnt.equalsIgnoreCase(stop)){
			ans.add(elmnt);
			elmnt = console.next();
		}
		return ans;
	}
	public ArrayList<Integer> nextIntCllct(String prompt, String stop){
		System.out.print(prompt);
		ArrayList<Integer> ans = new ArrayList<Integer>();
		int elmnt = 0;
		String sElmnt = "";
		while(!sElmnt.equalsIgnoreCase(stop)){
			if(console.hasNextInt()){
				elmnt = console.nextInt();
				sElmnt = "" + elmnt;
				if(sElmnt.equalsIgnoreCase(stop)){
					break;
				}
				ans.add(elmnt);
			} else {
				sElmnt = console.next();
				if(!sElmnt.equalsIgnoreCase(stop)){
					System.out.println("Sorry. Not Recognized. Try again.");
				}
			}
		}
		return ans;
	}
	public Map<String, Integer> nextStrIntHashMap(String prompt, int length){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i=0; i<length; i++){
			map.put(this.nextP(""), (Integer)this.nextIntP(""));
		}
		return map;
	}
	public Set<String> nextStrHashSet(String prompt, int length){
		Set<String> strSet = new HashSet<String>();
		for(int i=0; i<length; i++){
			strSet.add(this.nextP(""));
		}
		return strSet;
	}
}