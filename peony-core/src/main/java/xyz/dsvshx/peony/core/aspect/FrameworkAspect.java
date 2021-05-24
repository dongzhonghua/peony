package xyz.dsvshx.peony.core.aspect;

/**
 * 类方法切面，在这里实现方法增强的逻辑，主要是记录时间和方法，类信息
 *
 * @author dongzhonghua
 * Created on 2021-04-20
 */
public interface FrameworkAspect {
    /**
     * 调用链路入口监听
     */
    void entry();

    /**
     * 调用链路出口监听
     */
    void exit();
}
