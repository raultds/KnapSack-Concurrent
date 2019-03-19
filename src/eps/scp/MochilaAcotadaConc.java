/* ---------------------------------------------------------------
Práctica 2.
Código fuente : MochilaConcurrente.java
Grau Informàtica
49258369M ARNAU NADAL RIVERO.
49255165K RAUL ROCA JUNCÀ.
--------------------------------------------------------------- */

package eps.scp;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class MochilaAcotadaConc {

    private MochilaAcotada bok;
    private static final double NanosBySecond=1000000000.0;
    public MochilaAcotadaConc()
    {
        bok = new MochilaAcotada(400); // 400 dkg = 400 dag = 4 kg

        // making the list of items that you want to bring
        bok.add("map", 9, 150, 1);
        bok.add("compass", 13, 35, 1);
        bok.add("water", 153, 200, 3);
        bok.add("sandwich", 50, 60, 2);
        bok.add("glucose", 15, 60, 2);
        bok.add("tin", 68, 45, 3);
        bok.add("banana", 27, 60, 3);
        bok.add("apple", 39, 40, 3);
        bok.add("cheese", 23, 30, 1);
        bok.add("beer", 52, 10, 3);
        bok.add("suntan cream", 11, 70, 1);
        bok.add("camera", 32, 30, 1);
        bok.add("t-shirt", 24, 15, 2);
        bok.add("trousers", 48, 10, 2);
        bok.add("umbrella", 73, 40, 1);
        bok.add("waterproof trousers", 42, 70, 1);
        bok.add("waterproof overclothes", 43, 75, 1);
        bok.add("note-case", 22, 80, 1);
        bok.add("sunglasses", 7, 20, 1);
        bok.add("towel", 18, 12, 2);
        bok.add("socks", 4, 50, 1);
        bok.add("book", 30, 10, 2);

        SolveProblem();

    }

    public MochilaAcotadaConc(int MaxWeight, String InputItemsFile, int MaxThreads)
    {
        bok = new MochilaAcotada(MaxWeight, MaxThreads);


        if (ReadItems(InputItemsFile, bok)) {
            SolveProblem();
        }
    }

    public Boolean ReadItems(String InputItemsFile, MochilaAcotada bok)
    {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(InputItemsFile));
            String[] line;
            while ((line = reader.readNext()) != null) {
                //System.out.println("Item [name= " + line[0] + ", weight= " + line[1] + " , value=" + line[2] + " , bounding=" + line[3] +"]");
                bok.add(line[0], Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return(false);
        }

        return(true);
    }

    public void SolveProblem()
    {
        // Set start time.
        long startTime = System.nanoTime();

        // calculate the solution:
        List<Item> itemList = bok.calcSolution();

        // Set end time.
        long endTime   = System.nanoTime();
        double totalTime = endTime - startTime;

        // write out the solution in the standard output
        if (bok.isCalculated()) {
            NumberFormat nf  = NumberFormat.getInstance();

            System.out.println(
                    "Maximal weight           = " +
                            nf.format(bok.getMaxWeight() / 100.0) + " kg"
            );
            System.out.println(
                    "Total weight of solution = " +
                            nf.format(bok.getSolutionWeight() / 100.0) + " kg"
            );
            System.out.println(
                    "Total value              = " +
                            bok.getProfit()
            );
            System.out.println();
            System.out.println(
                    "You can carry te following materials " +
                            "in the knapsack:"
            );
            for (Item item : itemList) {
                if (item.getInKnapsack() > 0) {
                    System.out.format(
                            "%1$-10s %2$-23s %3$-3s %4$-5s %5$-15s \n",
                            item.getInKnapsack() + " unit(s) ",
                            item.getName(),
                            item.getInKnapsack() * item.getWeight(), "dag  ",
                            "(value = " + item.getInKnapsack() * item.getValue() + ")"
                    );
                }
            }

            System.out.println("\nCalculation time: "+ totalTime/NanosBySecond + " seconds." );

        } else {
            System.out.println(
                    "The problem is not solved. " +
                            "Maybe you gave wrong data."
            );
        }
    }

    public static void main(String[] args)
    {
        int maxThreads=0;
        int maxWeight=0;
        MochilaAcotadaConc mochila;
        if (args.length < 3)
            System.out.println("Error, ús ./MochilaAcotadaConc <pes maxim> <fitxer elements> <maxim threads>");
        else
            maxThreads=Integer.parseInt(args[2]);
            maxWeight=Integer.parseInt(args[0]);
            if(maxThreads>maxWeight)
                maxThreads=maxWeight+1;
            mochila = new MochilaAcotadaConc(maxWeight,args[1], maxThreads);
    }

} // class