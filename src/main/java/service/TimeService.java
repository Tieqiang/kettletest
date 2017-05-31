package service;


import com.google.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
import org.secnod.shiro.jaxrs.Auth;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heren on 2015/8/24.
 */
@Path("time")
@Produces(MediaType.APPLICATION_JSON)
public class TimeService {

    private HttpServletRequest httpServletRequest ;
    private ServletContext servletContext ;

    @Inject
    public TimeService(HttpServletRequest httpServletRequest, ServletContext servletContext) {
        this.httpServletRequest = httpServletRequest;
        this.servletContext = servletContext;
    }


    private List<DemoUser>  demoUsers = new ArrayList<>() ;
    @GET
    public Date getDate(){
        return new Date() ;
    }

    public TimeService() {

        for(int i = 0;i<10;i++){
            demoUsers.add(new DemoUser("username", "passord")) ;
        }
    }

    @Path("start-job")
    @GET
    public void startJob(){
        //1,初始化kettle环境
        try {
            initKettle();
            executeJob("/", "myTest");
        } catch (KettleException e) {
            e.printStackTrace();
        }

    }


    @GET
    @Path("get-req")
    @Produces("application/xml")
    @Consumes("application/xml")
    public String reqInfo(String req){
        return req ;
    }

    @GET
    @Path("list")
    public List<DemoUser> listDemoUsers(){
        return this.demoUsers ;
    }


    @GET
    @Path("auth")
    public List<DemoUser> testString(@Auth Subject user){
        DemoUser demoUser = new DemoUser(user.getPrincipal().toString(),user.getSession().getHost()) ;
        UsernamePasswordToken token   = new UsernamePasswordToken("zhao","123") ;
        try {
            user.login(token);
            System.out.println("认证成功");
        } catch (AuthenticationException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        }
        this.demoUsers.add(demoUser);
        return this.demoUsers;
    }

    @XmlRootElement
    class DemoUser{
        private String userName ;
        private String password ;

        public DemoUser(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 初始化环境
     */
    private void initKettle() throws KettleException {
        // 获得执行类的当前路径
        String user_dir = System.getProperty("user.dir");
        String kettleHome = servletContext.getRealPath(File.separator + "WEB-INF");

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

}


