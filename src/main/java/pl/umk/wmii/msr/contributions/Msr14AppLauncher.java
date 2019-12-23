package pl.umk.wmii.msr.contributions;

public interface Msr14AppLauncher extends AppLauncher {

    /**
     * Launches commits time grouper on specified project id and creates
     * related charts.
     *
     * @param args first argument (args[0]) is the project id, second
     *             argument (args[1]) is the directory where charts will
     *             be saved and third argument (args[2]) sets
     *             displayChart flag - if set to false, charts will not
     *             be displayed (default value is set to true)
     */
    void launchCommitsTimeGrouper(String[] args);

}
