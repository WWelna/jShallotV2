
public class Run {

	public static void main(String args[]) throws Exception {
		OnionGeneratorThread th1 = new OnionGeneratorThread(1); th1.start();
		OnionGeneratorThread th2 = new OnionGeneratorThread(2); th2.start();
		OnionGeneratorThread th3 = new OnionGeneratorThread(3); th3.start();
		OnionGeneratorThread th4 = new OnionGeneratorThread(4); th4.start();
		System.out.println("Started");
	}
}
