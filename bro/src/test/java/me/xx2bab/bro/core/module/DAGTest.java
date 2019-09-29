package me.xx2bab.bro.core.module;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DAGTest {

    private DAG<String> dag;

    @Before
    public void setup() {
        dag = new DAG<>();
    }

    @Test
    public void topoSort_Regular() {
        dag.addPrerequisite("A", "B");
        dag.addPrerequisite("B", null);
        List<String> list = dag.topologicalSort();
        Assert.assertEquals(list.get(0), "B");
        Assert.assertEquals(list.get(1), "A");
    }


    @Test
    public void topoSort_Complicated() {
        String[] list = new String[]{"1", "2", "3", "4", "5", "6"};
        for (int i = 0; i < list.length - 1; i++) {
            dag.addPrerequisite(list[i], list[i + 1]);
        }
        dag.addPrerequisite("6", null);
        List<String> result = dag.topologicalSort();
        for (int i = 0; i < list.length; i++) {
            Assert.assertEquals(list[list.length - 1 - i], result.get(i));
        }
    }

    @Test
    public void topoSort_Loop() {
        String[] list = new String[]{"1", "2", "3", "4", "5", "6"};
        for (int i = 0; i < list.length - 1; i++) {
            dag.addPrerequisite(list[i], list[i + 1]);
        }
        dag.addPrerequisite("6", "1");
        List<String> result = dag.topologicalSort();
        Assert.assertNull(result);
    }

}
