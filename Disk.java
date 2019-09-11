import java.util.*;

public class Disk
{
	public String block[] = new String[256];
	int blockSize = 512; // Maximum number of bytes per block
	public int diskPointer = 2; // Index number for file table string
	
	/*Constructor method*/
	public Disk()
	{
		char bitmap[] = new char[512];
		Arrays.fill(bitmap, '0');
		block[1] = String.valueOf(bitmap);
		block[1] = main.setChar(0, '1', block[1]);
		block[0] = "";
		block[1] = main.setChar(1, '1', block[1]);
	}
	
	/*This method reads a block from the disk*/
	public String read(int num)
	{
		return block[num];
	}
	
	/*This method writes a block to the disk*/
	public void write(String str, int num)
	{
		block[num] = str;
		block[1].toCharArray()[num] = '1';
	}
}
