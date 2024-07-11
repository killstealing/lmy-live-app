package org.lmy.live.im.router.provider.cluster;

import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;
import org.springframework.util.StringUtils;

import java.util.List;

public class ImRouterClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public ImRouterClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        String ip= (String) RpcContext.getContext().get("ip");
        if(StringUtils.isEmpty(ip)){
            throw new RuntimeException("ip can not be null");
        }
        List<Invoker<T>> list = list(invocation);
        Invoker<T> tInvoker = list.stream().filter(invoker -> {
            String s = invoker.getUrl().getHost() + ":" + invoker.getUrl().getPort();
            return s.equalsIgnoreCase(ip);
        }).findFirst().orElse(null);
        if(tInvoker==null){
            throw new RuntimeException("ip is invalid");
        }
        return tInvoker.invoke(invocation);
    }
}
