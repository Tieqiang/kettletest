package listener;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * Created by Administrator on 2017/5/31.
 */
public class KettleListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {


        //1,初始化kettle环境
        try {
            initKettle(servletContextEvent);
            executeJob("/", "myTest");
        } catch (KettleException e) {
            e.printStackTrace();
        }


    }

    /**
     * 初始化环境
     */
    private void initKettle(ServletContextEvent servletContextEvent) throws KettleException {
        // 获得执行类的当前路径
        String user_dir = System.getProperty("user.dir");
        String kettleHome = servletContextEvent.getServletContext().getRealPath(File.separator + "WEB-INF");

        System.out.println("kettle目录路径:"+kettleHome);
        // Kettle初始化需要修改相应的配置路径
        System.setProperty("user.dir", kettleHome);
        System.setProperty("KETTLE_HOME", kettleHome);
        // 运行环境初始化（设置主目录、注册必须的插件等）
        KettleEnvironment.init();
        // Kettle初始化完毕，还原执行类的当前路径
        System.setProperty("user.dir", user_dir);

    }

    /**
     * Kettle执行Job
     * @throws KettleException
     */
    public void executeJob(String dir, String jobname) throws KettleException {

        RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
        // 从文件读取登陆过的资源库信息
        repositoriesMeta.readData();
        // 选择登陆过的资源库
        RepositoryMeta repositoryMeta = repositoriesMeta.findRepository("java_kettle");
        // 获得资源库实例
        Repository repository = PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta, Repository.class);
        repository.init(repositoryMeta);
        // 连接资源库
        repository.connect("admin", "admin");

        RepositoryDirectoryInterface tree = repository.loadRepositoryDirectoryTree();
        RepositoryDirectoryInterface fooBar = tree.findDirectory(dir);
        JobMeta jobMeta = repository.loadJob(jobname, fooBar, null, null);
        // 执行指定作业
        Job job = new Job(repository, jobMeta);
        job.start();
        job.waitUntilFinished();
        Result result = job.getResult();
        result.getRows();
        if (job.getErrors() > 0) {
            throw new RuntimeException("There were errors during transformation execution.");
        }
        repository.disconnect();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
