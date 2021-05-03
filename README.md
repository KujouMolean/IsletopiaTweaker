 此插件实现梦幻之屿服务器大部分功能.

# 环境要求

1. PlotSquared5 地皮插件，此插件为收费插件，请自行获取；
2. MySQLPlayerDataBridge 跨服背包同步插件，同样为收费插件。（字节码依赖）
3. MySQL 8
4. BungeeCord 仅在Bungee模式下可用
5. BungeeSync 梦幻之屿的Bungee插件
6. Paper 服务端

# 配置要求

PlotSquared数据需要存储在MySql中，且数据库必须为servern, 例如server1。 服务器运行根目录必须为servern，bungeecord中配置的子服名称也必须为servern。
每个服务器只能有一个世界，世界名称必须为SkyWorld，且必须为地皮世界。 对于PlotSquared的配置，可以在config目录直接复制，复制完成后需要修改数据库配置。
MySql中需要创建minecraft数据库，servern数据库，编码都需要为UTF-8。

# 说明

此插件实现了梦幻之屿80%以上对自定义功能，经过了长期迭代，对环境要求过于苛刻，配置过程中可能还存在其他问题。 如果你对此插件感兴趣，可以向我直接发送邮件，我可以将开发环境打包给你。 如果有任何疑问，可以通过邮件或者issue咨询。

# 梦幻之屿服务器的整体架构

这可以帮助你快速理解插件开发的细节。

## 服务器的11个服务器

梦幻之屿服务器由1个WaterFall（BungeeCord）服务器加上10个Paper服务器构成。

其中8个Paper服务器是空岛服务器，是负责游戏进行的主要场地。 另外两个服务器中，一个是转发（Dispatcher）服务器，另一个是登陆（Login）服务器。

Waterfall 是反向代理服务器，让玩家无需手动切换服务器。

玩家进服首先需要接触的是WaterFall，WaterFall将玩家的连接数据首先转发到登陆服务器。

玩家登陆成功后，将其发送到转发服务器，转发服务器再将玩家转发到所需到空岛服务器中。

如果玩家是新手玩家，则转发到任意一个服务器中，领取岛屿并给予新手奖励。不是新手玩家则转发到上一次离开时的服务器。

## 服务器的4个物理机

服务器的11个服务器中，其中WaterFall和Login服务器位于物理机A。Dispatcher，八个空岛服以及MySQL数据库位于物理机B。

皮肤站以及玩家登陆数据位于物理机C，皮肤站通过frp转发到了物理机D。

（注意：玩家登陆数据存储在物理机C，玩家游戏数据存储在物理机B）

下面解释一下为什么要把服务器分布在四台物理机器上。

物理机B是服务器的核心，负责服务器的数据计算和存储，但不对外开放。仅有物理机A可以访问物理机B。由于物理机B负责大量工作，会经常性对其进行修改，其宕机概率较高。

将服务器分成物理机A，B后即使负责主要运算物理机B崩溃后，玩家也会被传送到物理机A的Login服务器上进行等待，服务器修复后，玩家又会被传送到空岛服务器。
这样做的另一个好处就是，对服务器进行重启操作不会影响服务器在线人数，玩家不会退出游戏，只是被传送到登陆服再传送回来。

皮肤站和玩家登陆位于物理机C上，但物理机C并没有DDOS防护，所以不对外开放，转发到物理机D。物理机D也没有DDOS防护，但通过这样做，即使物理机D被DDOS后，物理机A也可以通过直接访问物理机C的方式获取数据，
不至于皮肤站被攻击后无法登陆。 物理机C和D仅需了解，对服务器整体架构无太大影响。

## 8个完全相同的空岛服

服务器的八个完全相同的空岛服是用于充分利用服务器多线程，众所周知，"MC服务器只吃单核"， 虽然有点夸张，但对多核对利用率极低是不争事实。

对于8个完全相同对空岛服，无需开发相同的插件，所以它们共用一个插件：IsletopiaDispatcher

它们所在的目录名分别为server1、server2、server3......server8，它们都放在同一个目录下(必须，后续会提到)。

大致结构为：
/servers/  
.../dispatcher/  
.../server1/  
....../plugins/  
........./IsletopiaTweakers.jar  
....../server.properties  
.../server2/  
.../server3/  
.../server4/  
.../server5/  
.../server6/   
.../server7/  
.../server8/  

这些目录中仅有以下不同：
1. server.properties 中的端口不同
2. plugins/PlotSquared/storage.yml 中配置的数据库名不同
3. logs目录中的日志不同（这是当然的）
4. SkyWorld中所有内容不同

IsletopiaTweakers会通过运行目录，判断目前运行的服务器是哪一个服务器，所以目录名称十分重要。


## Waterfall 负责数据转发的反向代理服务器

这个除了服务器负责反向代理之外，还需要通知八个子服务器一些基本的信息，例如所有服务器的玩家列表。
IsletopiaTweakers插件中/src/main/java/com/molean/isletopiatweakers/infrastructure/individual/ServerInfoUpdater.java
这个类负责定时向Waterfall发送请求（通过BungeeCord提供的消息频道），及时更新服务器的玩家列表等信息。
转发服务器根据config.yml中配置的服务器名称来标识各个子服，所以配置中必须采用servern的格式，也就是server1、server2之类的。

## Dispatcher 负载均衡

Dispatcher服务器的一个重要功能是根据服务器近期的负载情况，把玩家转发到负载较低的服务器上。详情可以看IsletopiaDispatcher插件。

## PlotSquared 作为世界管理的空岛插件

服务器的世界配置比较特殊，插件也非常依赖这些配置，如果配置不正确，插件无法运作。

1.在bukkit.yml中将allow-end设置为false。
2.在server.properties中设置allow-nether为false，level-name为SkyWorld
3.在bukkit.yml中添加以下代码：
```yaml
worlds:
  SkyWorld:
  generator: PlotSquared
```
4.在plugins/PlotSquared/config/storage.yml中填写数据库信息，注意servern的数据库名必须为servern。请手动创建数据库。
5.在plugins/PlotSquared/config/settings.yml中修改选项
```yaml
teleport:
  # Teleport to your plot on death
  on-death: false
  # Teleport to your plot on login
  on-login: false
```
6.将plugins/PlotSquared/config/worlds.yml修改为：
```yaml
configuration_version: v5
worlds:
  SkyWorld:
    plot:
      height: 150
      biome: minecraft:forest
      size: 512
      filling: air
      auto_merge: false
      bedrock: false
      create_signs: false
      floor: air
    wall:
      filling: air
      block_claimed: air
      height: 150
      block: air
      place_top_block: true
    misc_spawn_unowned: false
    road:
      block: air
      height: 150
      width: 0
      flags: {}
    home:
      nonmembers: center
      default: center
    schematic:
      specify_on_claim: false
      on_claim: false
      file: 'null'
      schematics: []
    economy:
      prices:
        merge: 100
        sell: 100
        claim: 100
      use: false
    chat:
      enabled: false
      forced: false
    limits:
      max-members: 14
    world:
      max_height: 256
      gamemode: survive
      min_height: 1
      border: false
    event:
      spawn:
        egg: true
        breeding: true
        custom: true
    natural_mob_spawning: true
    mob_spawner_spawning: true
    flags:
      pve: true
      pvp: false
      explosion: true
      snow-form: true
      mob-cap: 256
      ice-melt: true
      ice-form: true
      coral-dry: true
      drop-protection: true
      block-burn: true
      block-ignition: true
      soil-dry: true
      mob-place: true
      mob-break: true
      red-stone: true
      animal-attack: false
      animal-interact: false
      use: '#signs, #stairs, #slabs'

```
7.plugins/PlotSquared/schematics/GEN_ROAD_SCHEMATIC/SkyWorld/plot.schematic 此文件为岛屿文件，可以通过worldedit导出schematic文件来自定义岛屿样式。
8.plugins/MysqlPlayerDataBridge/config.yml 中修改数据库配置。

致辞所有的特殊配置都完成了，对于开发插件而言，做完这些就可以结束了，对于开起一个服务器而言，还有一些工作。

你还需要做的事情有：
1.加入一个权限插件例如LuckPerm，给予玩家领取一个地皮的权限。
2.加入SkinsRestorer插件让玩家能够使用皮肤。
...

## 搭建开发环境

搭建完一个空岛服务器后，你只需再开启一个Waterfall服务器并加入BungeeSync插件（见我的另一个仓库）即可开始修改此插件。

由于MySQLPlayerDataBridge插件没有提供源码，你必须手动将其添加为依赖。

其他依赖均可通过maven自动下载。

## 插件的细节和技术解释

todo
