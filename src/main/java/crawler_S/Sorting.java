package crawler_S;

public class Sorting {
	
	private static long comparions = 0;
	private static long exchanges = 0;
	
	//change sequence  big->small => small -> big or reverse
	public static void changeSequence(int[] Array)
	{
		int times = Array.length/2;
		for(int i = 0; i < times; i++)
		{
			int temp = Array[i];
			Array[i] = Array[Array.length-i-1];
			Array[Array.length-i-1] = temp;
		}
			  
	}
	
	//bubble sort for Integer arrary
	//from small to big.
	public static void bubbleSort(int[] array)
	{
		for(int i = 0; i < array.length; i++)
			for(int j=1; j < array.length; j++)
				if(array[j-1] > array[j])
				{
					int temp;
					temp = array[j-1];
					array[j-1] = array[j];
					array[j] = temp;
					
				}
	}
	
	//bubble sort for the array and the following array
	public static void bubbleSortForTwo(int[] array, int[] follow)
	{
		for(int i = 0; i < array.length; i++)
			for(int j=1; j < array.length; j++)
				if(array[j-1] > array[j])
				{
					int temp1;
					temp1 = array[j-1];
					array[j-1] = array[j];
					array[j] = temp1;
					
					int temp2;
					temp2 = follow[j-1];
					follow[j-1] = follow[j];
					follow[j] = temp2;
					
				}
	}
	
	
	//from big to small
		public static void quickSort(float[] array, String[] follow, int left, int right)
		{
			if(right <= left) return;
			int i = partition(array, follow, left, right);
			quickSort(array, follow, left, i-1);
			quickSort(array, follow, i+1, right);
		}
	
	
	
	
	//from big to small
	public static void quickSort(int[] array, String[] follow, int left, int right)
	{
		if(right <= left) return;
		int i = partition(array, follow, left, right);
		quickSort(array, follow, left, i-1);
		quickSort(array, follow, i+1, right);
	}
	
	
	
	
	//from big to small
	public static void quickSort(int[] array, int[] follow, int left, int right)
	{
		if(right <= left) return;
		int i = partition(array, follow, left, right);
		quickSort(array, follow, left, i-1);
		quickSort(array, follow, i+1, right);
	}
	
	
	
	private static int partition(float[] a, String[] f, int left, int right)
	{
		int i = left-1;
		int j = right;
		while(true)
		{
			while(less(a[++i], a[right]));
			while(less(a[right], a[--j]))
				if(j == left) break;
			if(i >= j) break;
			exch(a, i, j);
			exch(f, i, j);
		}
		exch(a, i, right);
		exch(f, i, right);
		return i;
		
	}
	
	
	
	private static int partition(int[] a, String[] f, int left, int right)
	{
		int i = left-1;
		int j = right;
		while(true)
		{
			while(less(a[++i], a[right]));
			while(less(a[right], a[--j]))
				if(j == left) break;
			if(i >= j) break;
			exch(a, i, j);
			exch(f, i, j);
		}
		exch(a, i, right);
		exch(f, i, right);
		return i;
		
	}
	
	private static int partition(int[] a, int[] f, int left , int right)
	{
		int i = left-1;
		int j = right;
		while(true)
		{
			while(less(a[++i], a[right]));
			while(less(a[right], a[--j]))
				if(j == left) break;
			if(i >= j) break;
			exch(a, i, j);
			exch(f, i, j);
		}
		exch(a, i, right);
		exch(f, i, right);
		return i;
	}
	
	
	private static boolean less(float x, float y)
	{
		comparions++;
		return (x > y);
	}
	
	
	private static boolean less(int x, int y)
	{
		comparions++;
		return (x > y);
	}
	
	private static void exch(float[] a, int i, int j)
	{
		exchanges++;
		float swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
	
	private static void exch(String[] a, int i, int j)
	{
		exchanges++;
		String swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
	
	
	private static void exch(int[] a, int i, int j)
	{
		exchanges++;
		int swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}

}
