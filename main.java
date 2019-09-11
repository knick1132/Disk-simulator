import java.io.*;
import java.util.*;

// Author : Nicholas Kiv
// Class : 4348.501
// Description :
//    This program simulates three different disk file allocation
//    methods. It will allow the user to choose an allocator method
//    and perform disk operations on a simulated disk.

public class main
{
	public static int mode;
	public static Disk simDisk = new Disk();

	/*Main method*/
	public static void main(String[] args)
	{
		
		
		Scanner read = new Scanner(System.in);
		int userOption = 0; // Scanned menu option input
		/*This loop operates the user interface using as switch statement*/
		
		System.out.println("Allocation type: ");
		System.out.println("1) contiguous");
		System.out.println("2) indexed");
		System.out.println("3) chained");
		System.out.println();
		mode = read.nextInt();
		read.nextLine();
		System.out.println();
		
		
		while(userOption != 8)
		{
			displayMenu();
			System.out.println("Choice: ");
			userOption = read.nextInt();
			read.nextLine();
			switch(userOption)
			{
			case 1 :
				System.out.print("File name: ");
				System.out.print(displayFile(read.nextLine()));
				break;
			case 2 :
				displayTable();
				break;
			case 3 :
				displayMap();
				break;
			case 4 : 
				System.out.println("Block: ");
				System.out.println(simDisk.read(read.nextInt()));
				read.nextLine();
				break;
			case 5 :
				System.out.println("Copy to: ");
				try
				{
					PrintWriter fileOut = new PrintWriter(read.nextLine());
					System.out.println("Copy from: ");
					fileOut.print(displayFile(read.nextLine()));
					fileOut.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 6 :
				try {
					System.out.println("Copy from: ");
					readFile(read.nextLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 7 : 
				System.out.println("File name: ");
				deleteFile(read.nextLine());
				break;
			case 8 :
				System.out.println("Program terminated");
				System.exit(0);
				break;
			default :
				continue;
			}
		}
	}
	
	//reads the file for the other modes
	public static void readFile(String name) throws IOException
	{
		
		//intinalize file
		System.out.println(name);
		File input = new File(name);
		FileInputStream fileRead = new FileInputStream(input);
		byte[] data = new byte[(int) input.length()];
		fileRead.read(data);
		fileRead.close();
		String fileData = new String(data, "UTF-8");
		System.out.println("Copy to: " + name);
		int FileBlockSize = (fileData.length()/512) + 1;
		int StartBlock = simDisk.diskPointer;
		
		if(mode == 1) {
			if(fileData.length() > (512 * 10))
				throw new IOException();
			/*This loop writes file input into the simulated disk file table*/
			for(int i = 0; i < FileBlockSize; i++)
			{
				if ((i+1) * 512 > fileData.length()) // Checks for out of bounds
				{
					simDisk.write(fileData.substring( i * 512, fileData.length()), simDisk.diskPointer);
					simDisk.block[1] = setChar(simDisk.diskPointer++ , '1', simDisk.block[1]); // Updates bitmap
				}
				else
				{
					simDisk.write(fileData.substring( i * 512, (i + 1) * 512), simDisk.diskPointer);
					simDisk.block[1] = setChar(simDisk.diskPointer++ , '1', simDisk.block[1]); // Updates bitmap
				}
			}
			simDisk.block[0] = simDisk.block[0] + name + " " + StartBlock + " " + FileBlockSize + " ";
			System.out.println("File " + name + " copied");
		} else if(mode == 2) {
			if(fileData.length() > (512 * 10))
				throw new IOException();
			
			Random rand = new Random();
			int index = rand.nextInt(256 - 2) + 2;
			simDisk.block[1] = setChar(index , '1', simDisk.block[1]);
			simDisk.write("" , index);
	
			for(int i = 0; i < FileBlockSize; i++)
			{
				if ((i+1) * 512 > fileData.length()) // Checks for out of bounds
				{
					int blockLoc = rand.nextInt(256 - 2) + 2;
					
					do {
						blockLoc = rand.nextInt(256 - 2) + 2;
					}while(simDisk.read(1).charAt(blockLoc) == '1');
					
					simDisk.write(simDisk.read(index) + blockLoc + " " , index);
					simDisk.write(fileData.substring( i * 512, fileData.length()), blockLoc);
					simDisk.block[1] = setChar(blockLoc , '1', simDisk.block[1]); // Updates bitmap
				}
				else
				{
					int blockLoc = rand.nextInt(256 - 2) + 2;
					
					do {
						blockLoc = rand.nextInt(256 - 2) + 2;
					}while(simDisk.read(1).charAt(blockLoc) == '1');
					
					simDisk.write(simDisk.read(index) + blockLoc + " ", index);
					simDisk.write(fileData.substring( i * 512, (i + 1) * 512), blockLoc);
					simDisk.block[1] = setChar(blockLoc , '1', simDisk.block[1]); // Updates bitmap
				}
			}
			simDisk.block[0] = simDisk.block[0] + name + " " + index + " ";
			System.out.println("File " + name + " copied");
		} else if(mode == 3) {
			
			
			if(fileData.length() > (511 * 10))
				throw new IOException();
			
			FileBlockSize = (fileData.length()/511) + 1;
			System.out.println(FileBlockSize + " " + fileData.length());
			Random rand = new Random();
			
			int blockLoc = rand.nextInt(256 - 2) + 2;
			int index = blockLoc;
			
			simDisk.block[0] = simDisk.block[0] + name + " " + index + " " + FileBlockSize + " ";
			
			/*This loop writes file input into the simulated disk file table*/
			for(int i = 0; i < FileBlockSize; i++)
			{
				//System.out.println(i);
	
				do {
					blockLoc = rand.nextInt(256 - 2) + 2;
				}while(simDisk.read(1).charAt(blockLoc) == '1');
				
				if ((i+1) * 511 > fileData.length()) // Checks for out of bounds
				{	
					simDisk.write(999 + "~" + fileData.substring( i * 511, fileData.length()), index);
					//System.out.println(index + "----");
					simDisk.block[1] = setChar(index , '1', simDisk.block[1]); // Updates bitmap
				}
				else
				{
					//System.out.println(index + "++++");
					simDisk.write(blockLoc + "~" + fileData.substring( i * 511, (i + 1) * 511), index);
					simDisk.block[1] = setChar(index , '1', simDisk.block[1]); // Updates bitmap
				}
				
				index = blockLoc;
			}
			System.out.println("File " + name + " copied");
		}
	}
	
	/*This method displays the user interface*/
	public static void displayMenu()
	{
		System.out.println("1) Display a file");
		System.out.println("2) Display the file table");
		System.out.println("3) Display the free space bitmap");
		System.out.println("4) Display a disk block");
		System.out.println("5) Copy a file from the simulation to a file on the real system");
		System.out.println("6) Copy a file from the real system to a file in the simulation");
		System.out.println("7) Delete a file");
		System.out.println("8) Exit");
		System.out.println();
	}
	
	/*This method sets a character at an index in an array*/
	public static String setChar(int i, char ch, String str) {
		
		char[] charTemp = str.toCharArray();
		charTemp[i] = ch;
		String updatedString = String.valueOf(charTemp);
		return updatedString;
	}
	
	/*This method displays the file table*/
	public static void displayTable()
	{
		
		if(mode == 1) {
			System.out.println("File Name        Start Block  Length");
			String table[] = simDisk.read(0).split(" ");
			for (int i = 0; i < table.length - 1; i+=3)
			{
				System.out.println(String.format("%-17s", table[i]) + String.format("%-13s",table[i+1]) + String.format("%-6s", table[i+2]));
			}
		}else if(mode == 2) {
			System.out.println("File Name        Index Block");
			String table[] = simDisk.read(0).split(" ");
			for (int i = 0; i < table.length - 1; i+=2)
			{
				System.out.println(String.format("%-17s", table[i]) + String.format("%-13s",table[i+1]));
			} 
				
		}else if (mode == 3) {
			System.out.println("File Name        Start Block  Length");
			String table[] = simDisk.read(0).split(" ");
			for (int i = 0; i < table.length - 1; i+=3)
			{
				System.out.println(String.format("%-17s", table[i]) + String.format("%-13s",table[i+1]) + String.format("%-6s", table[i+2]));
			}
		}
	}
	
	/*This method displays the bitmap*/
	public static void displayMap()
	{
		
		for (int i = 0; i < 16; i++)
		{
			System.out.println(simDisk.read(1).substring((i * 16), (i * 16) + 16));
		}
	}
	
	/*This method displays a single file from simulation*/
	public static String displayFile(String name)
	{
		String fileOutput= "";
		String table[] = simDisk.read(0).split(" ");
		
		if(mode == 1) {
			for (int i = 0; i < table.length; i+=3)
			{
				if (name.equals(table[i]))
				{
					int startIndex = Integer.parseInt(table[i+1]);
					int length = Integer.parseInt(table[i+2]);
					for (int j = 0; j < length; j++)
					{
						fileOutput += simDisk.read(j + startIndex);
					}
					System.out.println();
					break;
				}
			}
		}else if (mode == 2) {
			for (int i = 0; i < table.length; i+=2)
			{
				if (name.equals(table[i]))
				{
					String index[] = simDisk.read(Integer.parseInt(table[i + 1])).split(" ");
					for(int j = 0; j < index.length; j++ ) {
						fileOutput += simDisk.read(Integer.parseInt(index[j]));
					}
					System.out.println();
					break;
				}
			}
		}else if (mode == 3) {
			
			for (int i = 0; i < table.length; i++)
			{
				if (name.equals(table[i])) {		
					
					int pointer = Integer.parseInt(simDisk.read(Integer.parseInt(table[i+1])).split("~")[0]);
					fileOutput +=  simDisk.read(Integer.parseInt(table[i+1])).split("~")[1];
					int length = Integer.parseInt(table[i+2]);
					
					while(pointer != 999)
					{
						fileOutput += simDisk.read(pointer).split("~")[1];
						pointer = Integer.parseInt(simDisk.read(pointer).split("~")[0]);
					}
					break;
				}
			}
		}
		
		return fileOutput;
	}
	
	/*This method deletes a single file from simulation*/
	public static void deleteFile(String name)
	{
		String table[] = simDisk.read(0).split(" ");
		if(mode == 1) {
			for (int i = 0; i < table.length; i+=3)
			{
				if (name.equals(table[i]))
				{
					int startIndex = Integer.parseInt(table[i+1]);
					int length = Integer.parseInt(table[i+2]);
					for (int j = 0; j < length; j++)
					{
						simDisk.write("", j + startIndex);
						simDisk.block[1] = setChar(j + startIndex , '0', simDisk.block[1]); // Updates bitmap
					}
					String delete = table[i] + " " + table[i + 1] + " " + table[i + 2] + " ";
					System.out.println(delete);
					simDisk.block[0] = simDisk.block[0].replace(delete ,"");
					break;
					
				}
			}
		}else if(mode == 2) {
			for (int i = 0; i < table.length; i+=2)
			{
				if (name.equals(table[i]))
				{
					
					String index[] = simDisk.read(Integer.parseInt(table[i + 1])).split(" ");
					simDisk.write("", Integer.parseInt(table[i + 1]));
					for(int j = 0; j < index.length; j++ ) {
						simDisk.write("", Integer.parseInt(index[j]));
						simDisk.block[1] = setChar(Integer.parseInt(index[j]) , '0', simDisk.block[1]);
					}
					simDisk.block[1] = setChar(Integer.parseInt(table[i + 1]) , '0', simDisk.block[1]);
						
					String delete = table[i] + " " + table[i + 1] + " ";
					simDisk.block[0] = simDisk.block[0].replace(delete ,"");
					System.out.println();
					break;
				}
			}
		} else if (mode == 3) {
		
			for (int i = 0; i < table.length; i++)
			{
				if (name.equals(table[i])) {		
					int fileOutput = 0;
					int pointer = Integer.parseInt(simDisk.read(Integer.parseInt(table[i+1])).split("~")[0]);
					simDisk.block[1] = setChar(Integer.parseInt(table[i+1]) , '0', simDisk.block[1]);
					fileOutput =  pointer;
					simDisk.write("", Integer.parseInt(table[i + 1]));
					
					while(pointer != 999)
					{
					
						pointer = Integer.parseInt(simDisk.read(pointer).split("~")[0]);
						if(pointer == 999) {
							simDisk.block[1] = setChar(fileOutput , '0', simDisk.block[1]);
							break;
						}
						simDisk.write("", fileOutput);
						simDisk.block[1] = setChar(fileOutput , '0', simDisk.block[1]);
						fileOutput =  pointer;
					}
					String delete = table[i] + " " + table[i + 1] + " " + table[i + 2] + " ";
					System.out.println(delete);
					simDisk.block[0] = simDisk.block[0].replace(delete ,"");
					break;
				}
			}
			
		}
	}
}
