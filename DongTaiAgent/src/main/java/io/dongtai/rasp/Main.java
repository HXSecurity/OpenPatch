package io.dongtai.rasp;

import io.dongtai.rasp.classloader.AgentClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.jar.JarFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @author owefsad
 */
public class Main {

    public static void main(String[] args) {
        Options attachOptions = new Options();

        attachOptions.addOption(build("p", "pid", "webserver process id"));
        attachOptions.addOption(build("m", "mode", "Agent mode: Log4jPatch or Log4jVerify"));
        attachOptions.addOption(build("e", "engine", "DongTai Engine path, eg: ./DongTai-Engine.jar"));

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine result = null;
        try {
            result = parser.parse(attachOptions, args);
            if (result.hasOption("p") && result.hasOption("m") && result.hasOption("e")) {
                String pid = result.getOptionValue("p");
                String enginePath = result.getOptionValue("e");
                File engineFile = new File(enginePath);
                if (engineFile.exists()) {
                    System.out.println(engineFile.getAbsolutePath());
                    AttachLauncher.attach(pid, engineFile.getAbsolutePath() + "&" + result.getOptionValue("m"));
                } else {
                    System.err.println(
                            "[io.dongtai.rasp] DongTai Engine not exist, please use absolute path. eg: /opt/DongTai-Engine.jar");
                }
            } else {
                formatter.printHelp("java -jar agent.jar", attachOptions, true);
            }
        } catch (Throwable t) {
            System.err.println("[io.dongtai.rasp] Start DongTai Agent failed, exception stack trace: ");
            t.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * install agent with premain
     *
     * @param args DongTai engine path
     * @param inst inst
     */
    public static void premain(String args, Instrumentation inst) {
        start(inst, args);
    }

    /**
     * install agent with attach
     *
     * @param args DongTai engine path
     * @param inst inst
     */
    public static void agentmain(String args, Instrumentation inst) {
        start(inst, args);
    }

    private static void start(Instrumentation inst, String args) {
        final AgentClassLoader classLoader;
        try {
            String[] agentArgs = parseArgs(args);
            // 检查engine 是否存在
            System.out.println("try to fix log4j2's vul. engine path is " + agentArgs[0]);
            JarFile engineFile = new JarFile(agentArgs[0]);
            classLoader = new AgentClassLoader("rasp", agentArgs[0]);
            Class<?> raspOfClass = classLoader.loadClass("io.dongtai.rasp.engine.Engine");
            raspOfClass.getMethod("install", Instrumentation.class, String.class).invoke(null, inst, agentArgs[1]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建option参数对象
     *
     * @param opt     短参数名
     * @param longOpt 长参数名
     * @param desc    参数描述
     * @return 参数对象
     */
    public static Option build(String opt, String longOpt, String desc) {
        return Option.builder(opt).longOpt(longOpt).hasArg().desc(desc).build();
    }

    /**
     * 解析 args 参数
     */
    public static String[] parseArgs(String args) {
        return args.split("&", 2);
    }
}
