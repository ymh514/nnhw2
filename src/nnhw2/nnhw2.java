package nnhw2;

import java.io.*;
import java.math.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

import nnhw2.Paint;

public class nnhw2 extends JFrame {

	static int frameSizeX = 800;
	static int frameSizeY = 800;
	static int neuralAmount = 3;
	static int studyCycle = 0;
	static ArrayList<float[]> inputArray = new ArrayList<float[]>();
	static ArrayList<float[]> sortedArray = new ArrayList<float[]>();
	static ArrayList<float[]> tempArray = new ArrayList<float[]>();
	static ArrayList<float[]> trainArray = new ArrayList<float[]>();
	static ArrayList<float[]> testArray = new ArrayList<float[]>();
	static ArrayList<float[]> initialWeight = new ArrayList<float[]>();

	static float[] yOutput = new float[neuralAmount];
	
	static int sortedNewDesire =0;

	public static void inputFileChoose(String[] args) throws IOException {

		String FileName = "/Users/Terry/Documents/workspace/datasets/hw2/xor.txt";
		FileReader fr = new FileReader(FileName);
		BufferedReader br = new BufferedReader(fr);// 在br.ready反查輸入串流的狀況是否有資料

		String txt;
		while ((txt = br.readLine()) != null) {
			/*
			 * If there is space before split(), it will cause the error So, we
			 * could to use trim() to remove the space at the beginning and the
			 * end. Then split the result, which doesn't include the space at
			 * the beginning and the end. "\\s+" would match any of space, as
			 * you don't have to consider the number of space in the string
			 */
			String[] token = txt.trim().split("\\s+");// <-----背起來
			// String[] token = txt.split(" ");//<-----original split
			float[] token2 = new float[token.length];// 宣告float[]

			try {
				for (int i = 0; i < token.length; i++) {
					token2[i] = Float.parseFloat(token[i]);
				} // 把token(string)轉乘token2(float)
				inputArray.add(token2);// 把txt裡面內容先切割過在都讀進array內
			} catch (NumberFormatException ex) {
				System.out.println("Sorry Error...");
			}
		}
		fr.close();// 關閉檔案

	}
	
	public static void sortInputArray(ArrayList<float[]> inputArray) {
		/*
		 * 1. set loop times = inputArray's dataamount 
		 * 2. in while loop we have to dynamic change loop times cause we had 
		 * 	remove some data in the array to reduce loop times 
		 * 3. set a variable-standardDesire is mean the first data's desire ,
		 *  then use it to check one by one ,if found someone is as same as 
		 *  the standardDesire, put this data to sortedArray, so on ,we can get a
		 *  sorted array which's desire is from 1 to number of class
		 * 4. everytime move a item to sortedArray , raise iRestFlag and set i to
		 * 	0, then it will run loop from beginning 
		 * 5. when inputarray left only 1 item must set as -1, or the last data's
		 * 	desire will be set one more number
		 *  
		 */
		int inputArraySize = inputArray.size();
		int iRestFlag=0;
		System.out.println("--------- Start sort ---------");
		System.out.println("This is inputarray's size : "+inputArraySize);
		whileloop:
		while (true) {
			int standardDesire = (int) inputArray.get(0)[inputArray.get(0).length - 1];// set the first one's desire as standard
			System.out.println("Now the standartDesire is  : "+standardDesire);
			
			for (int i = 0; i < inputArray.size(); i++) {
				if(iRestFlag ==1){
					i=0;
				}
				if ((int)inputArray.get(i)[inputArray.get(i).length - 1] == standardDesire) {
					inputArray.get(i)[inputArray.get(i).length - 1]=sortedNewDesire;
					sortedArray.add(inputArray.get(i));
					inputArray.remove(i);
					iRestFlag = 1;
				}
				else{
					iRestFlag =0;
				}
				if(inputArray.size()==1){//the last data need set i=-1 to prevent after forloop's i++
					i=-1;
				}
			}
			if(inputArray.size()==0){
				System.out.println("--------- Sort done! ---------");
				System.out.println("");
				break whileloop;
			}
			else{
				sortedNewDesire ++;//count desire 
			}
		}
		System.out.println("The max sorted desire : "+sortedNewDesire);
	}	
	
	private static void putInputToTemp(ArrayList<float[]> sorteArray) {
		int arrayInputAmount = sortedArray.size();
		Random rand = new Random();
		while (arrayInputAmount != 0) {
			int n = rand.nextInt(arrayInputAmount) + 0;
			tempArray.add(sortedArray.get(n));
			sortedArray.remove(n);// del input to prevent get same data
			arrayInputAmount--;
		}

	}

	private static void separateTemp(ArrayList<float[]> tempArray) {

		int totalamount = tempArray.size();
		int tocalamount = Math.round((float) (totalamount * 2) / 3);
		int totestamount = totalamount - tocalamount;

		while (tocalamount != 0) {
			trainArray.add(tempArray.get(0));
			tempArray.remove(0);
			tocalamount--;
		}
		System.out.println("train amount : " + trainArray.size());
		while (totestamount != 0) {
			testArray.add(tempArray.get(0));
			tempArray.remove(0);
			totestamount--;
		}
		System.out.println("test amount : " + testArray.size());
	}
	
	public static void genarateInitialWeight(){
		Random rand = new Random();
		for(int i=0;i<neuralAmount;i++){
			float[] token = new float[trainArray.size()];
			for(int j=0 ; j<trainArray.size();j++){
				token[j]=rand.nextFloat()+1f;
			}
			initialWeight.add(token);
		}	
	}
	
	public static void calOutputValue(ArrayList<float[]> array,ArrayList<float[]> initialWeight){		
		/*
		 * 1. use for to run neuralAmount times to get y
		 * 2. when its last loop get last output 
		 * 3. use yOutput which generated by upper loop and do cal
		 *   with weight to get z the last output
		 *   notice : for(j) loop's yOutput[j+1] 
		 * 4. the latest value of yOutput is outputz
		 */
		int x0=-1;
		for(int i =0;i<neuralAmount;i++){
			if(i!=neuralAmount-1){
				float sum=0f;
				sum=x0*initialWeight.get(i)[0];
				for(int j=0;j<array.get(studyCycle).length-1;j++){
					sum += array.get(studyCycle)[j]*initialWeight.get(i)[j+1];//must j+1 to match right
				}
				yOutput[i] = (float) (1/(1+Math.exp(-sum)));
				System.out.println("y"+i+" output is : "+yOutput[i]);
			}
			else{
				float sumZ=0f;
				sumZ=x0*initialWeight.get(i)[0];
				for(int j=0;j<yOutput.length-1;j++){
					sumZ += yOutput[j]*initialWeight.get(i)[j+1];//must j+1 to match right 
				}
				yOutput[i] = (float) (1/(1+Math.exp(-sumZ)));
				System.out.println("y"+i+"(z) output is : "+yOutput[i]);					
			}
		}
	}
		
	private static void genarateFrame(ArrayList<float[]> inputArray, int countClass) {
		JFrame frame = new JFrame();

		frame.setVisible(true);// just set visible
		frame.setLocation(100, 100);// set the frame show location
		frame.setSize(frameSizeX, frameSizeY);// set the frame size
		frame.setResizable(false);

		Paint trypaint = new Paint(inputArray, countClass);
		frame.add(trypaint);// add paint(class) things in to the frame
	}
	

	
	private static int countClass(ArrayList<float[]> inputArray) {
		/*
		 * 1. put first class's type into classTypes 1st place 2. if next line's
		 * class diffrent with 1st class so go on next if 3. search classTypes's
		 * all class to judge if all are diffrent rais addFlag 4. if addFlag
		 * raised, add this new class into classType
		 */		
		/*
		int addFlag = 0;
		classTypes.add((int) inputArray.get(0)[(inputArray.get(0).length) - 1]);

		for (int i = 0; i < inputArray.size(); i++) {
			if (classTypes.get(0) != (int) inputArray.get(i)[(inputArray.get(i).length) - 1]) {
				for (int j = 0; j < classTypes.size(); j++) {
					if (classTypes.get(j) != inputArray.get(i)[(inputArray.get(0).length) - 1]) {
						addFlag = 1;
					} else {
						addFlag = 0;
					}
				}
				if (addFlag == 1) {
					classTypes.add((int) inputArray.get(i)[(inputArray.get(0).length) - 1]);
				}
			}
		}
		return classTypes.size();
		*/
		return 0;
	}

	
	public static void main(String[] args) throws IOException {

		inputFileChoose(args);

		sortInputArray(inputArray);

		putInputToTemp(sortedArray);// copy to temp with random

		separateTemp(tempArray);// separate to train and test set,set 2/3 as
									// train set 1/3 as test set
		
		System.out.println("trainArray's datas : ");
		printArrayData(trainArray);
		/*
		System.out.println("testArray's datas : ");
		printArrayData(testArray);
		*/
		genarateInitialWeight();
		System.out.println("this cylce loop's class is : "+trainArray.get(0)[trainArray.get(0).length-1]);
		calOutputValue(trainArray,initialWeight);
		
		genarateFrame(trainArray, sortedNewDesire+1);
	}
	
	public static void printArrayData(ArrayList<float[]> showArray) {
		for (int i = 0; i < showArray.size(); i++) {
			for (int j = 0; j < showArray.get(i).length; j++) {
				System.out.print(showArray.get(i)[j] + "\t");
			}
			System.out.println("");
		}
		System.out.println("");
	}

}