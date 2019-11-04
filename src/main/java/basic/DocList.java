package basic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DocList {
    private Set<Doc> aSet = new HashSet<>();

    public DocList() {

    }


    public void addDoc(Doc aDoc) {
        if (this.getaSet().contains(aDoc)) {
            this.findDocById(aDoc.getDocId());
        }
//		if(this.getaSet().contains(aDoc.getDocId()));
        else {
            this.aSet.add(aDoc);
        }
    }

    public int getSize() {
        int size = 0;
        if (this.aSet.isEmpty()) {
            size = 0;
        } else {
            size = this.aSet.size();
        }
        return size;
    }

    public Set<Doc> getaSet() {
        return aSet;
    }

    public void findDocById(String id) {
        Iterator<Doc> it = aSet.iterator();
        while (it.hasNext()) {
            Doc tempDoc = it.next();
            if (tempDoc.getDocId().equals(id)) {
                tempDoc.addFreq();
            }
        }
    }


    public String toFreqs() {
        String tempStr = "{";
        Iterator<Doc> it = aSet.iterator();

        while (it.hasNext()) {
            Doc tempDoc = it.next();
            tempStr = tempStr + tempDoc.getDocFreq() + ",";
        }

        tempStr = tempStr + "}";
        return tempStr;

    }

    public long getC() {
        int[] sum = new int[100];
        for (int m = 0; m < 100; m++) {
            sum[m] = 0;
        }
        //
        Iterator<Doc> it = this.getaSet().iterator();
        while (it.hasNext()) {
            Doc tempDoc = it.next();
            int tempFreq = tempDoc.getDocFreq();
            sum[tempFreq + 1]++;
        }
        int sumC = 0;
        for (int j = 1; j < 100; j++) {
            sumC = sumC + sum[j] * (j - 1) * j / 2;

        }
        return sumC;
    }

    @Override
    public String toString() {
        String total = "";
        Iterator<Doc> it = this.getaSet().iterator();
        while (it.hasNext()) {
            Doc tempDoc = it.next();
            total = total + tempDoc.getDocId() + " "+tempDoc.getDocDegree()+"\n";
        }
        return total;
    }

    public long getSum() {
        int n = this.getSize();
        long sumAllDegree = 0;
        float sumAllDegreeDev = 0;
        Iterator<Doc> it = this.getaSet().iterator();
        while (it.hasNext()) {
            Doc tempDoc = it.next();
            int tempDegree = tempDoc.getDocDegree();
            sumAllDegree = sumAllDegree + tempDegree;
            sumAllDegreeDev = (float) (sumAllDegreeDev + (1.0) / (float) (tempDegree));
        }
//
//		System.out.println("sumAllDegree is : " + sumAllDegree);
//		System.out.println("sumAllDegreeDev: " + sumAllDegreeDev);
        long total = (long) (sumAllDegree * sumAllDegreeDev * ((1.0) / (float) this.getC()) * (n - 1) / (2 * n));
        return total;
    }
}
