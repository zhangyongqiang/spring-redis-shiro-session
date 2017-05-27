# spring-redis-shiro-session
         shiro 框架没有用tomcat的session，而是重新实现了一套。所以系统一旦引入shiro后，采用传统的tomcat session共享机制是无效的，必须采用面向shiro 的session共享。         网上针对“shiro session共享”的文章比较多，但是大同小异，基本是基于redis实现的。但是该套实现，代码质量非常差（10几个java文件，都快吓晕），并且redis的连接没有基于spring，而是在java代码中硬编码，这几乎无法容忍。         为此，本人基于spring连接redis，吸取了他们文章的思想，重新写了一套解决方案。代码量已精简到他们的1/3，并且考虑到了他们没有考虑到的问题，绝对是精华！
