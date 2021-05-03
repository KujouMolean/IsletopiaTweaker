## 此插件实现梦幻之屿服务器大部分功能.

### 环境要求
1. PlotSquared5 地皮插件，此插件为收费插件，请自行获取；
2. MySQLPlayerDataBridge 跨服背包同步插件，同样为收费插件。（字节码依赖）
3. MySQL 8
4. BungeeCord 仅在Bungee模式下可用
5. BungeeSync 梦幻之屿的Bungee插件
6. Paper 服务端

### 配置要求
PlotSquared数据需要存储在MySql中，且数据库必须为servern, 例如server1。
服务器运行根目录必须为servern，bungeecord中配置的子服名称也必须为servern。
每个服务器只能有一个世界，世界名称必须为SkyWorld，且必须为地皮世界。
对于PlotSquared的配置，可以在config目录直接复制，复制完成后需要修改数据库配置。
MySql中需要创建minecraft数据库，servern数据库，编码都需要为UTF-8。

### 说明
此插件实现了梦幻之屿80%以上对自定义功能，经过了长期迭代，对环境要求过于苛刻，配置过程中可能还存在其他问题。
如果你对此插件感兴趣，可以向我直接发送右键，我可以将开发环境打包给你。
如果有任何疑问，可以通过邮件或者issue咨询。



