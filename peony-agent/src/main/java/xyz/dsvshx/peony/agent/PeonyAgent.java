package xyz.dsvshx.peony.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.peony.agent.config.AgentConfigHolder;
import xyz.dsvshx.peony.agent.jvm.Metric;
import xyz.dsvshx.peony.agent.loader.PeonyClassLoader;

/**
 * @author dongzhonghua
 * Created on 2021-04-09
 */
@Slf4j
public class PeonyAgent {
    /**
     * jvm启动时运行这个函数
     */
    public static void premain(String agentOps, Instrumentation instrumentation) {
        try {
            log.info(">>>>>>>进入premain，agent参数：{}", agentOps);
            AgentConfigHolder.holdConfig(agentOps);

            String agentSpyPath = AgentConfigHolder.getAgentSpyPath();
            File agentSpyFile = new File(agentSpyPath);
            if (!agentSpyFile.exists()) {
                System.out.println("Agent jar file does not exist: " + agentSpyFile);
                return;
            }
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(agentSpyFile));

            File agentCoreFile = new File(AgentConfigHolder.getAgentCorePath());
            if (!agentCoreFile.exists()) {
                System.out.println("Agent jar file does not exist: " + agentCoreFile);
                return;
            }

            PeonyClassLoader peonyClassLoader = new PeonyClassLoader(new URL[] {agentCoreFile.toURI().toURL()});
            Class<?> peonyClassFileTransformer =
                    peonyClassLoader.loadClass("xyz.dsvshx.peony.instrumentation.PeonyClassFileTransformer");
            Constructor<?> declaredConstructor = peonyClassFileTransformer.getDeclaredConstructor(String.class);

            instrumentation.addTransformer((ClassFileTransformer) declaredConstructor.newInstance(agentSpyPath));

            // jvm信息，暂时关闭
            // processJvmInfo();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void processJvmInfo() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            Metric.printMemoryInfo();
            Metric.printGCInfo();
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }


    public static void agentmain(String agentOps, Instrumentation instrumentation) {
        try {

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
