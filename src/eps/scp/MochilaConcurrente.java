/* ---------------------------------------------------------------
Práctica 2.
Código fuente : MochilaConcurrente.java
Grau Informàtica
49258369M ARNAU NADAL RIVERO.
49255165K RAUL ROCA JUNCÀ.
--------------------------------------------------------------- */

package eps.scp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MochilaConcurrente implements Runnable{
    static List<Item> itemList  = new ArrayList<Item>();
    static int maxWeight        = 0;
    static int maxThreads       = 0;
    static int solutionWeight   = 0;
    static int profit           = 0;
    static boolean calculated   = false;
    static List<Integer> prev;

    protected List<Integer> llistaThread;
    protected int inici;
    protected int finalThread;
    protected int elem;

    public MochilaConcurrente() {}

    public MochilaConcurrente(int _maxWeight) {
        setMaxWeight(_maxWeight);
    }

    public MochilaConcurrente(List<Item> _itemList) {
        setItemList(_itemList);
    }

    public MochilaConcurrente(List<Item> _itemList, int _maxWeight) {
        setItemList(_itemList);
        setMaxWeight(_maxWeight);
    }

    public MochilaConcurrente(int iniciThread, int finalThread, int elem){
        this.inici=iniciThread;
        this.finalThread=finalThread;
        this.elem=elem;
        this.llistaThread=new ArrayList<Integer>();
    }

    private int getInici(){
        return this.inici;
    }

    private int getFinal(){
        return this.finalThread;
    }

    private int getElem(){
        return this.elem;
    }

    private List<Integer> getList(){ return this.llistaThread;}
    // calculate the solution of 0-1 knapsack problem with dynamic method:

    public List<Item> calcSolution() {
        int n = itemList.size();
        setInitialStateForCalculation();

        int arrayCarrega[];
        MochilaConcurrente[] mochiles=new MochilaConcurrente[maxThreads];
        Thread[] threads=new Thread[maxThreads];

        if (n > 0  &&  maxWeight > 0) {
            List< List<Integer> > c = new ArrayList< List<Integer> >();
            List<Integer> curr = new ArrayList<Integer>();

            c.add(curr);
            for (int j = 0; j <= maxWeight; j++)
                curr.add(0);
            for (int i = 1; i <= n; i++) {
                prev = curr;
                c.add(curr = new ArrayList<Integer>());
                arrayCarrega=equilibrateProcesses();
                for (int j = 0; j < maxThreads; j++) {
                    int suma=arrayCarrega[j]+arrayCarrega[j+1];
                    mochiles[j]=new MochilaConcurrente(arrayCarrega[j],suma, i);
                    arrayCarrega[j+1]=suma;
                    threads[j]=new Thread(mochiles[j]);
                    threads[j].start();
                } // for (j...)
                try {
                    for (int x = 0; x < maxThreads; x++) {
                        threads[x].join();
                        curr.addAll(mochiles[x].getList());
                    }
                } catch (InterruptedException e){
                    System.out.println("ERROR");
                }
            } // for (i...)


            profit = curr.get(maxWeight);
            for (int i = n, j = maxWeight; i > 0  &&  j >= 0; i--) {
                int tempI   = c.get(i).get(j);
                int tempI_1 = c.get(i-1).get(j);
                if ((i == 0  &&  tempI > 0) || (i > 0  &&  tempI != tempI_1))
                {
                    Item iH = itemList.get(i-1);
                    int  wH = iH.getWeight();
                    iH.setInKnapsack(1);
                    j -= wH;
                    solutionWeight += wH;
                }
            } // for()
            calculated = true;
        } // if()
        return itemList;
    }
    @Override
    public void run() {

        for (int j = getInici(); j < getFinal(); j++) {
            if (j > 0) {
                int wH = itemList.get(getElem() - 1).getWeight();
                if (wH > j)
                    getList().add(prev.get(j));
                else
                    getList().add(Math.max(prev.get(j), itemList.get(getElem() - 1).getValue() + prev.get(j - wH)));
            } else {
                getList().add(0);
            }
        }
    }
    // add an item to the item list
    public void add(String name, int weight, int value) {
        if (name.equals(""))
            name = "" + (itemList.size() + 1);
        itemList.add(new Item(name, weight, value));
        setInitialStateForCalculation();
    }

    // add an item to the item list
    public void add(int weight, int value) {
        add("", weight, value); // the name will be "itemList.size() + 1"!
    }

    // remove an item from the item list
    public void remove(String name) {
        for (Iterator<Item> it = itemList.iterator(); it.hasNext(); ) {
            if (name.equals(it.next().getName())) {
                it.remove();
            }
        }
        setInitialStateForCalculation();
    }

    // remove all items from the item list
    public void removeAllItems() {
        itemList.clear();
        setInitialStateForCalculation();
    }


    public int getProfit() {
        if (!calculated)
            calcSolution();
        return profit;
    }

    public int getSolutionWeight() {return solutionWeight;}
    public boolean isCalculated() {return calculated;}
    public int getMaxWeight() {return maxWeight;}

    public void setMaxWeight(int _maxWeight) {
        maxWeight = Math.max(_maxWeight, 0);
    }

    public void setMaxThreads(int max_Threads) {
        maxThreads = Math.max(max_Threads, 1);
    }

    public void setItemList(List<Item> _itemList) {
        if (_itemList != null) {
            itemList = _itemList;
            for (Item item : _itemList) {
                item.checkMembers();
            }
        }
    }

    // set the member with name "inKnapsack" by all items:
    private void setInKnapsackByAll(int inKnapsack) {
        for (Item item : itemList)
            if (inKnapsack > 0)
                item.setInKnapsack(1);
            else
                item.setInKnapsack(0);
    }

    // set the data members of class in the state of starting the calculation:
    protected void setInitialStateForCalculation() {
        setInKnapsackByAll(0);
        calculated     = false;
        profit         = 0;
        solutionWeight = 0;
    }

    private int[] equilibrateProcesses(){
        int arrayCarrega[]=new int[maxThreads+1];       //equilibrar carrega procesos
        arrayCarrega[0]=0;
        int valorsProcessos=(maxWeight+1)/maxThreads;
        for (int i=1; i<maxThreads+1; i++){
            arrayCarrega[i]=valorsProcessos;
        }

        if(valorsProcessos*maxThreads<maxWeight+1){
            for(int i=1; i<=(maxWeight+1)-(valorsProcessos*maxThreads); i++){
                arrayCarrega[i]+=1;
            }
        }
        return arrayCarrega;
    }
}