package cn.luern0313.lson;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import cn.luern0313.lson.annotation.field.LsonAddPrefix;
import cn.luern0313.lson.annotation.field.LsonAddSuffix;
import cn.luern0313.lson.annotation.field.LsonBooleanFormatAsNumber;
import cn.luern0313.lson.annotation.field.LsonBooleanFormatAsString;
import cn.luern0313.lson.annotation.field.LsonDateFormat;
import cn.luern0313.lson.annotation.field.LsonJoinArray;
import cn.luern0313.lson.annotation.field.LsonNumberFormat;
import cn.luern0313.lson.annotation.field.LsonPath;
import cn.luern0313.lson.annotation.field.LsonReplaceAll;
import cn.luern0313.lson.annotation.field.LsonSplitString;
import cn.luern0313.lson.annotation.method.LsonCallMethod;
import cn.luern0313.lson.element.LsonObject;

import static org.junit.Assert.*;

/**
 * 被 luern 创建于 2021/12/14.
 */
public class DeserializationTest
{
    String json = "{\"dataMap\":{\"dataKey3\":{\"canRead\":true},\"dataKey4\":{\"canRead\":true},\"dataKey1\":{\"canRead\":true},\"dataKey2\":{\"canRead\":true}},\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":true},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Mo by Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":12.99}}}";
    String json2 = "{\"code_value\":0.1,\"message\":\"success\",\"data\":{\"user_id\":9,\"timestamp\":1599665659,\"name\":\"luern0313\",\"lv\":6,\"coin\":5463.15,\"video\":[{\"video_id\":1,\"title\":\"1\",\"author\":{\"name\":\"luern0313\",\"uid\":9},\"img\":\"http://1.png\",\"state\":{\"like\":2.0,\"coin\":6}},{\"video_id\":2,\"title\":\"2\",\"author\":{\"name\":\"luern0313\",\"uid\":9},\"img\":\"http://2.png\",\"state\":{\"like\":23,\"coin\":66}},{\"video_id\":3,\"title\":\"3\",\"author\":{\"name\":\"luern0313\",\"uid\":9},\"img\":\"http://3.png\",\"state\":{\"like\":233,\"coin\":666}}],\"medal\":[[0,0,0,0],[0,0,1,4],[5,0,6,0]]}}";
    String json3 = "  { \"a\" : {  \"aa\": \"1\"},    \"video_author_uid\"   : {\"qqq\": {\"www\":123}}   }  ";
    String json4 = "{\"songs\": [{\"name\": \"送你一瓣的雪花\",\"id\": 8800,\"pst\": 0,\"t\": 0,\"ar\": [{\"id\": 3701,\"name\": \"黎明\",\"tns\": [],\"alias\": []}],\"alia\": [],\"pop\": 25.0,\"st\": 0,\"rt\": \"600902000005384374\",\"fee\": 8,\"v\": 12,\"crbt\": null,\"cf\": \"\",\"al\": {\"id\": 11232,\"name\": \"心爱\",\"picUrl\": \"https://p2.music.126.net/bjo6nKSXVizkSfx55D0kjw==/80264348828353.jpg\",\"tns\": [],\"pic\": 80264348828353},\"dt\": 269767,\"h\": {\"br\": 320000,\"fid\": 0,\"size\": 10816118,\"vd\": 0.131678},\"m\": {\"br\": 160000,\"fid\": 0,\"size\": 5425490,\"vd\": -2.65076E-4},\"l\": {\"br\": 96000,\"fid\": 0,\"size\": 3268821,\"vd\": -2.65076E-4},\"a\": null,\"cd\": \"1\",\"no\": 10,\"rtUrl\": null,\"ftype\": 0,\"rtUrls\": [],\"djId\": 0,\"copyright\": 0,\"s_id\": 0,\"mark\": 0,\"originCoverType\": 0,\"single\": 0,\"noCopyrightRcmd\": null,\"mv\": 0,\"rtype\": 0,\"rurl\": null,\"mst\": 9,\"cp\": 563013,\"publishTime\": 754675200000}],\"privileges\": [{\"id\": 114514,\"fee\": 8,\"payed\": 0,\"st\": 0,\"pl\": 128000,\"dl\": 0,\"sp\": 7,\"cp\": 1,\"subp\": 1,\"cs\": false,\"maxbr\": 320000,\"fl\": 128000,\"toast\": false,\"flag\": 256,\"preSell\": false,\"playMaxbr\": 320000,\"downloadMaxbr\": 320000,\"freeTrialPrivilege\": {\"resConsumable\": false,\"userConsumable\": false},\"chargeInfoList\": [{\"rate\": 128000,\"chargeUrl\": null,\"chargeMessage\": null,\"chargeType\": 0}, {\"rate\": 192000,\"chargeUrl\": null,\"chargeMessage\": null,\"chargeType\": 1}, {\"rate\": 320000,\"chargeUrl\": null,\"chargeMessage\": null,\"chargeType\": 1}, {\"rate\": 999000,\"chargeUrl\": null,\"chargeMessage\": null,\"chargeType\": 1}]}],\"code\": 200}";
    String json5 = "{\"itemsId\": 10011100, \"name\": \"世嘉 VOCALOID 初音未来 景品Q版手办 国内独家发售\", \"brief\": \"(๑╹◡╹)ﾉ\", \"img\": [\"//i0.hdslb.com/bfs/mall/mall/5c/12/5c124b6d7c79b0dff14ca566703ee087.png\", \"//i0.hdslb.com/bfs/mall/mall/46/96/4696c9a813f93e12ac0a79f083b24ed2.png\", \"//i0.hdslb.com/bfs/mall/mall/cb/6f/cb6f34262faeadc98e853d8db9d54b28.jpg\", \"//i0.hdslb.com/bfs/mall/mall/c2/c3/c2c3cdda09848d20c896aac5e48648c1.jpg\", \"//i0.hdslb.com/bfs/mall/mall/df/75/df752b3e2c07eb4610102d3f363fe2da.jpg\"], \"price\": 8800, \"type\": 0, \"saleType\": 0, \"jumpLinkType\": 0}";
    String json6 = "[{\"salesperson_id\": 1, \"product_id\": 1, \"sales_figures\": 17}, {\"salesperson_id\": 1, \"product_id\": 2, \"sales_figures\": 5}, {\"salesperson_id\": 1, \"product_id\": 3, \"sales_figures\": 13}, {\"salesperson_id\": 1, \"product_id\": 4, \"sales_figures\": 14}]";
    String json7 = "{\"code\": 0, \"msg\": \"\", \"message\": \"\", \"data\": {\"business_id\": 478923153122814944, \"status\": 0, \"lottery_time\": 1610899200, \"lottery_at_num\": 0, \"lottery_feed_limit\": 1, \"first_prize\": 1, \"second_prize\": 0, \"third_prize\": 0, \"first_prize_cmt\": \"TAITO 雷姆 景品手办\", \"second_prize_cmt\": \"\", \"third_prize_cmt\": \"\", \"first_prize_pic\": \"https://i0.hdslb.com/bfs/album/7837f72c636bf24986ba651a7ea4b815a49794ff.jpg\", \"second_prize_pic\": \"\", \"third_prize_pic\": \"\", \"need_post\": 1, \"business_type\": 1, \"sender_uid\": 675987417, \"prize_type_first\": {\"type\": 0, \"value\": {\"stype\": 0}}, \"prize_type_second\": {\"type\": 0, \"value\": {\"stype\": 0}}, \"prize_type_third\": {\"type\": 0, \"value\": {\"stype\": 0}}, \"pay_status\": 0, \"ts\": 1610546649, \"lottery_id\": 56567, \"_gt_\": 0}}";
    String json8 = "{\"code\":0,\"message\":\"success\",\"result\":[{\"date\":\"4-5\",\"date_ts\":1617552000,\"day_of_week\":1,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/320be8e374bb4b6d590b16f7059fedc99d4e20d1.png\",\"delay\":0,\"ep_id\":398808,\"favorites\":0,\"follow\":1,\"is_published\":1,\"pub_index\":\"第2话\",\"pub_time\":\"20:45\",\"pub_ts\":1617626700,\"season_id\":37924,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/aacadf1c196e3a4266f7d55b06cc31e2c936ddd1.png\",\"title\":\"这爱情有点奇怪\",\"url\":\"https://www.bilibili.com/bangumi/play/ss37924\"}]},{\"date\":\"4-6\",\"date_ts\":1617638400,\"day_of_week\":2,\"is_today\":0,\"seasons\":[]},{\"date\":\"4-7\",\"date_ts\":1617724800,\"day_of_week\":3,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/a7625fc1f4cf13635664c65cf863a2017694b8d4.png\",\"delay\":0,\"ep_id\":395834,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第13话\",\"pub_time\":\"17:35\",\"pub_ts\":1617788100,\"season_id\":36362,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/d647736e0c0d3897904cb325663b4b8e704dc0f2.png\",\"title\":\"愤怒的审判\",\"url\":\"https://www.bilibili.com/bangumi/play/ss36362\"}]},{\"date\":\"4-8\",\"date_ts\":1617811200,\"day_of_week\":4,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1cd26af47cf9d9ca045ec36f56ce14a66867438d.png\",\"delay\":0,\"ep_id\":399420,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第1话-第2话\",\"pub_time\":\"18:00\",\"pub_ts\":1617876000,\"season_id\":38353,\"season_status\":13,\"square_cover\":\"ht" +
            "tp://i0.hdslb.com/bfs/bangumi/image/4bd7b0a4c270ce62ca2adf3e18487a96e4fde92e.png\",\"title\":\"通灵王\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38353\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/a1b98209234a65542b1a56b228d847206b4df51c.png\",\"delay\":0,\"ep_id\":399414,\"favorites\":0,\"follow\":1,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"19:00\",\"pub_ts\":1617879600,\"season_id\":38220,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/729ec8eaa3df7924cc336f5d11f898ab2911c605.png\",\"title\":\"极主夫道\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38220\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/68bb99512e16639649be4864dea5925900d966cc.png\",\"delay\":0,\"ep_id\":399472,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"23:45\",\"pub_ts\":1617896700,\"season_id\":37575,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/b398350d4d16d7ef0e5a019da08153f40b1e937a.png\",\"title\":\"佐贺偶像是传奇 卷土重来\",\"url\":\"https://www.bilibili.com/bangumi/play/ss37575\"}]},{\"date\":\"4-9\",\"date_ts\":1617897600,\"day_of_week\":5,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/f0796e37abac25ba2aa9f23120646aaa9a3d5ea7.png\",\"delay\":0,\"ep_id\":374633,\"favorites\":0,\"follow\":1,\"is_published\":1,\"pub_index\":\"第13话\",\"pub_time\":\"21:30\",\"pub_ts\":1617975000,\"season_id\":36167,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/61d38dd807c95a5ba8746181c0e05f45856abd63.png\",\"title\":\"转生成蜘蛛又怎样！\",\"url\":\"https://www.bilibili.com/bangumi/pla" +
            "y/ss36167\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1c02521ba4973df6916cd97eef5b5a9881a3f0cf.png\",\"delay\":0,\"ep_id\":399496,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第1话-第2话\",\"pub_time\":\"22:00\",\"pub_ts\":1617976800,\"season_id\":38233,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/4b5584c031267f48d45d020c5cb7e1f0f83705ce.png\",\"title\":\"SSSS.电光机王\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38233\"}]},{\"date\":\"4-10\",\"date_ts\":1617984000,\"day_of_week\":6,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/fbd2849172a5ed7b206ab1f5269db6705bc49352.png\",\"delay\":0,\"ep_id\":398687,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第14话\",\"pub_time\":\"00:30\",\"pub_ts\":1617985800,\"season_id\":36282,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/f04f5e59f09f73c750fdb2ccac9d69c0c5b08a49.png\",\"title\":\"巴克·亚罗/BACK ARROW\",\"url\":\"https://www.bilibili.com/bangumi/play/ss36282\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/13c8a0111b0937c779f85cc671458492d2440c27.png\",\"delay\":0,\"ep_id\":391743,\"favorites\":0,\"follow\":1,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"02:30\",\"pub_ts\":1617993000,\"season_id\":38239,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/78564db74c7e8262f133f11a9ab9b833104f2fcd.png\",\"title\":\"蔚蓝反射 澪\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38239\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/8fc921e44f03ed314ae50be1e180db931820a04e.png\",\"delay\":0,\"ep_id\":399484,\"fa" +
            "vorites\":0,\"follow\":1,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"02:55\",\"pub_ts\":1617994500,\"season_id\":38238,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1dd58cd2ac38beb59f7aed2979457f1d86645bb7.png\",\"title\":\"美丽新世界 The Animation/美好世界\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38238\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/b750b45aa179546339eabe6050b50af06227df50.png\",\"delay\":0,\"ep_id\":391346,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第2话\",\"pub_time\":\"03:00\",\"pub_ts\":1617994800,\"season_id\":38241,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/844d8ff40d5bee64d298b2251cddd7db0896a7c8.png\",\"title\":\"纯白之音\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38241\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/357e75a1fb3e17a80b49f60af35f5d211d639921.jpg\",\"delay\":0,\"ep_id\":386361,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第47话\",\"pub_time\":\"05:30\",\"pub_ts\":1618003800,\"season_id\":33942,\"season_status\":2,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/01a56659dec4dd58518f31645fa6d3f4bb55754e.png\",\"title\":\"海獭波乐 5th\",\"url\":\"https://www.bilibili.com/bangumi/play/ss33942\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/92af0125cc637db12351fb381304277bc407921c.png\",\"delay\":0,\"ep_id\":399660,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第2话\",\"pub_time\":\"07:30\",\"pub_ts\":1618011000,\"season_id\":38240,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/713c1b1f57accfe8d19d2f" +
            "00e4d65e7a3a2b042d.png\",\"title\":\"卡片战斗先导者overDress\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38240\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/16ce8c4c49d20a33ceda34a9b420adc9bf01f10e.png\",\"delay\":0,\"ep_id\":391717,\"favorites\":0,\"follow\":1,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"21:30\",\"pub_ts\":1618061400,\"season_id\":38229,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/30b0a09b5f940cbc39cc583a2f1941cc94d4afbc.png\",\"title\":\"打了300年的史莱姆，不知不觉就练到了满级\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38229\"}]},{\"date\":\"4-11\",\"date_ts\":1618070400,\"day_of_week\":7,\"is_today\":1,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/b6eff89926452a21153512c43275c0d2368a27e1.png\",\"delay\":0,\"ep_id\":399521,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"01:25\",\"pub_ts\":1618075500,\"season_id\":38230,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/187747b6e306a0d56f1bdb789f060b2dda6ab348.png\",\"title\":\"伊甸星原 EDENS ZERO\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38230\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/d1ccd334e5659fc4e0f7bd4a5a98d12450a529b2.png\",\"delay\":0,\"ep_id\":399874,\"favorites\":0,\"follow\":0,\"is_published\":1,\"pub_index\":\"第1话\",\"pub_time\":\"21:00\",\"pub_ts\":1618146000,\"season_id\":37486,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/8fc7339eb925f65673b212b6ebfeafb5cd09ad15.png\",\"title\":\"大运动会 重启\",\"url\":\"https://www.bilibili.com/bangumi/play/ss37486\"},{\"cov" +
            "er\":\"http://i0.hdslb.com/bfs/bangumi/image/18d7c875ddf0ba4e90893cfa95ad2ba5684d7906.png\",\"delay\":0,\"ep_id\":399508,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第1话-第2话\",\"pub_time\":\"22:00\",\"pub_ts\":1618149600,\"season_id\":38237,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/4ba0cbc720745cdbd701a49ffc3d8c4b8f93dc67.png\",\"title\":\"龙先生，想要买个家 / 龙族买房\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38237\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/025ec0f296798602b48337a4e4149c6b8db85aa7.png\",\"delay\":0,\"ep_id\":395228,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第1话-第2话\",\"pub_time\":\"23:00\",\"pub_ts\":1618153200,\"season_id\":38234,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/cd84ca2237b47d7b5b9bc9dd61e3a21ccb08e32e.png\",\"title\":\"装甲重拳/MEGALOBOX 第二季\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38234\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/ef10d28a7f875528c38702ac9d5ba03fee3d4eda.png\",\"delay\":0,\"ep_id\":395959,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第13话\",\"pub_time\":\"23:00\",\"pub_ts\":1618153200,\"season_id\":38254,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/5c8d117e799847770bd5357596ee0d3b5191a99a.png\",\"title\":\"忧国的莫里亚蒂 后半\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38254\"}]},{\"date\":\"4-12\",\"date_ts\":1618156800,\"day_of_week\":1,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/320be8e374bb4b6d590b16f7059fedc99d4e20d1.png\",\"delay\":0,\"ep_id\":39" +
            "8809,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第3话\",\"pub_time\":\"20:45\",\"pub_ts\":1618231500,\"season_id\":37924,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/aacadf1c196e3a4266f7d55b06cc31e2c936ddd1.png\",\"title\":\"这爱情有点奇怪\",\"url\":\"https://www.bilibili.com/bangumi/play/ss37924\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/8c25d35568026e33528a9dc08b513eafe4f6973d.png\",\"delay\":0,\"ep_id\":393394,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第1话-第2话\",\"pub_time\":\"22:30\",\"pub_ts\":1618237800,\"season_id\":38243,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/e0c1492987acab546ecb3be4aeea3cbdabf45966.png\",\"title\":\"烧窑的话也要马克杯\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38243\"}]},{\"date\":\"4-13\",\"date_ts\":1618243200,\"day_of_week\":2,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1650b8923434efe7ccf7ce796647d4b1fb6d9758.png\",\"delay\":0,\"ep_id\":399396,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第1话\",\"pub_time\":\"00:00\",\"pub_ts\":1618243200,\"season_id\":38259,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/b28f25ecc524b8a22368bf4806101f627e55211e.png\",\"title\":\"影宅\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38259\"}]},{\"date\":\"4-14\",\"date_ts\":1618329600,\"day_of_week\":3,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/7e4dc7490fff2b5d3b1936ff76f8d35f7d0398fc.png\",\"delay\":0,\"ep_id\":399546,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\"" +
            ":\"第1话-第2话\",\"pub_time\":\"00:00\",\"pub_ts\":1618329600,\"season_id\":38236,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/6ad37a072b87a3a4b94a025ca8ffcf3266c7e13b.png\",\"title\":\"圣女魔力无所不能\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38236\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/a7625fc1f4cf13635664c65cf863a2017694b8d4.png\",\"delay\":0,\"ep_id\":395835,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第14话\",\"pub_time\":\"17:35\",\"pub_ts\":1618392900,\"season_id\":36362,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/d647736e0c0d3897904cb325663b4b8e704dc0f2.png\",\"title\":\"愤怒的审判\",\"url\":\"https://www.bilibili.com/bangumi/play/ss36362\"}]},{\"date\":\"4-15\",\"date_ts\":1618416000,\"day_of_week\":4,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1cd26af47cf9d9ca045ec36f56ce14a66867438d.png\",\"delay\":0,\"ep_id\":399608,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第3话\",\"pub_time\":\"18:00\",\"pub_ts\":1618480800,\"season_id\":38353,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/4bd7b0a4c270ce62ca2adf3e18487a96e4fde92e.png\",\"title\":\"通灵王\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38353\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/a1b98209234a65542b1a56b228d847206b4df51c.png\",\"delay\":0,\"ep_id\":399415,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第2话\",\"pub_time\":\"19:00\",\"pub_ts\":1618484400,\"season_id\":38220,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/729ec" +
            "8eaa3df7924cc336f5d11f898ab2911c605.png\",\"title\":\"极主夫道\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38220\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/68bb99512e16639649be4864dea5925900d966cc.png\",\"delay\":0,\"ep_id\":399473,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第2话\",\"pub_time\":\"23:45\",\"pub_ts\":1618501500,\"season_id\":37575,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/b398350d4d16d7ef0e5a019da08153f40b1e937a.png\",\"title\":\"佐贺偶像是传奇 卷土重来\",\"url\":\"https://www.bilibili.com/bangumi/play/ss37575\"}]},{\"date\":\"4-16\",\"date_ts\":1618502400,\"day_of_week\":5,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/f0796e37abac25ba2aa9f23120646aaa9a3d5ea7.png\",\"delay\":0,\"ep_id\":374634,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第14话\",\"pub_time\":\"21:30\",\"pub_ts\":1618579800,\"season_id\":36167,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/61d38dd807c95a5ba8746181c0e05f45856abd63.png\",\"title\":\"转生成蜘蛛又怎样！\",\"url\":\"https://www.bilibili.com/bangumi/play/ss36167\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1c02521ba4973df6916cd97eef5b5a9881a3f0cf.png\",\"delay\":0,\"ep_id\":399498,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第3话\",\"pub_time\":\"22:00\",\"pub_ts\":1618581600,\"season_id\":38233,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/4b5584c031267f48d45d020c5cb7e1f0f83705ce.png\",\"title\":\"SSSS.电光机王\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38233\"}]},{\"date\":\"4-17\",\"" +
            "date_ts\":1618588800,\"day_of_week\":6,\"is_today\":0,\"seasons\":[{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/fbd2849172a5ed7b206ab1f5269db6705bc49352.png\",\"delay\":0,\"ep_id\":398688,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第15话\",\"pub_time\":\"00:30\",\"pub_ts\":1618590600,\"season_id\":36282,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/f04f5e59f09f73c750fdb2ccac9d69c0c5b08a49.png\",\"title\":\"巴克·亚罗/BACK ARROW\",\"url\":\"https://www.bilibili.com/bangumi/play/ss36282\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/13c8a0111b0937c779f85cc671458492d2440c27.png\",\"delay\":0,\"ep_id\":391744,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第2话\",\"pub_time\":\"02:30\",\"pub_ts\":1618597800,\"season_id\":38239,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/78564db74c7e8262f133f11a9ab9b833104f2fcd.png\",\"title\":\"蔚蓝反射 澪\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38239\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/8fc921e44f03ed314ae50be1e180db931820a04e.png\",\"delay\":0,\"ep_id\":399485,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第2话\",\"pub_time\":\"02:55\",\"pub_ts\":1618599300,\"season_id\":38238,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/1dd58cd2ac38beb59f7aed2979457f1d86645bb7.png\",\"title\":\"美丽新世界 The Animation/美好世界\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38238\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/b750b45aa179546339eabe6050b50af06227df50.png\",\"delay\":0,\"ep_id\":391347,\"favorites\":0,\"follow\":0," +
            "\"is_published\":0,\"pub_index\":\"第3话\",\"pub_time\":\"03:00\",\"pub_ts\":1618599600,\"season_id\":38241,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/844d8ff40d5bee64d298b2251cddd7db0896a7c8.png\",\"title\":\"纯白之音\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38241\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/357e75a1fb3e17a80b49f60af35f5d211d639921.jpg\",\"delay\":0,\"ep_id\":386362,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第48话\",\"pub_time\":\"05:30\",\"pub_ts\":1618608600,\"season_id\":33942,\"season_status\":2,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/01a56659dec4dd58518f31645fa6d3f4bb55754e.png\",\"title\":\"海獭波乐 5th\",\"url\":\"https://www.bilibili.com/bangumi/play/ss33942\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/92af0125cc637db12351fb381304277bc407921c.png\",\"delay\":0,\"ep_id\":399661,\"favorites\":0,\"follow\":0,\"is_published\":0,\"pub_index\":\"第3话\",\"pub_time\":\"07:30\",\"pub_ts\":1618615800,\"season_id\":38240,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/713c1b1f57accfe8d19d2f00e4d65e7a3a2b042d.png\",\"title\":\"卡片战斗先导者overDress\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38240\"},{\"cover\":\"http://i0.hdslb.com/bfs/bangumi/image/16ce8c4c49d20a33ceda34a9b420adc9bf01f10e.png\",\"delay\":0,\"ep_id\":391718,\"favorites\":0,\"follow\":1,\"is_published\":0,\"pub_index\":\"第2话\",\"pub_time\":\"21:30\",\"pub_ts\":1618666200,\"season_id\":38229,\"season_status\":13,\"square_cover\":\"http://i0.hdslb.com/bfs/bangumi/image/30b0a09b5f940cbc39cc583a2f1941cc94d4afbc.png\",\"title\":\"打了300年的史莱姆，不知不觉就练到了满级\",\"url\":\"https://www.bilibili.com/bangumi/play/ss38229\"}]}]}";
    String json9 = "{\"a\":\"{\\\"b\\\":456}\"}";

    @Test
    public void test()
    {
        aBaseModel<UserModel<UserModel.UserVideoModel>> aBaseModel = LsonUtil.fromJson(LsonUtil.parse(json2), new TypeReference<aBaseModel<UserModel<UserModel.UserVideoModel>>>(){}, 1, "");
        System.out.println();
    }

    private static class aBaseModel<T>
    {
        @LsonPath("a.a")
        ArrayList<Integer> a;

        @LsonNumberFormat(digit = 1)
        @LsonPath
        String codeValue;

        @LsonBooleanFormatAsNumber(equal = {0.1, 123}, notEqual = {0.2, 0.1})
        @LsonPath("code_value")
        Boolean is_success;

        @LsonPath
        String message;

        @LsonBooleanFormatAsString(equal = {"success", "986456"}, notEqual = {"success", "fail"})
        @LsonPath("message")
        Boolean is_message;

        @LsonSplitString(value = "cc", arrayType = LinkedList.class)
        @LsonPath(value = "message", preClass = String.class)
        LinkedList<?> q;

        @LsonSplitString("cc")
        @LsonPath(value = "message", preClass = String.class)
        String[] qq;

        @LsonJoinArray(", ")
        @LsonAddPrefix("++")
        @LsonPath(value = "data.video[*].title", preClass = ArrayList.class)
        String text;

        @LsonPath("data.medal")
        LinkedList<LinkedList<Integer>> medals;

        @LsonPath("data")
        T data;

        private static Object aa(LsonObject map)
        {
            return map.toString();
        }

        public aBaseModel(int q, String a)
        {
            System.out.println(a);
        }

        enum Type
        {
            USER,
            UP
        }
    }

    private static class UserModel<D>
    {
        @LsonPath
        int userId;

        @LsonPath
        StringBuilder name;

        @LsonAddPrefix("LV")
        @LsonPath
        String lv;

        @LsonDateFormat(value = "yyyy-MM-dd HH:mm", mode = LsonDateFormat.LsonDateFormatMode.SECOND)
        @LsonPath("timestamp")
        String date;

        @LsonPath("timestamp")
        Date date1;

        @LsonPath("timestamp")
        java.sql.Date date2;

        @LsonReplaceAll(regex = "硬", replacement = "软", order = 3)
        @LsonAddSuffix(value = "个", order = 4)
        @LsonNumberFormat(digit = 0, mode = LsonNumberFormat.NumberFormatMode.DOWN, order = 1)
        @LsonAddPrefix(value = "硬妹币: ", order = 2)
        @LsonPath
        String coin; // 5463.15

        @LsonPath("video[0,2].title")
        ArrayList<String> videoTitle1;

        @LsonPath("video[1].title")
        ArrayList<String> videoTitle2;

        @LsonPath("video")
        ArrayList<D> videos;

        @LsonPath("video[0]")
        D video;

        @LsonPath("medal")
        ArrayList<ArrayList<Integer>> medals;

        @LsonCallMethod(timing = {LsonCallMethod.CallMethodTiming.AFTER_DESERIALIZATION, LsonCallMethod.CallMethodTiming.AFTER_SERIALIZATION})
        void a()
        {
            videoTitle1.set(1, "9999");
            System.out.println(videoTitle1);
        }

        private static class UserVideoModel
        {
            @LsonPath()
            String videoId;

            @LsonPath()
            String title;

            @LsonNumberFormat(digit = 0, mode = LsonNumberFormat.NumberFormatMode.DOWN)
            @LsonAddSuffix("个")
            @LsonPath("state.like")
            String like;

            @LsonNumberFormat(digit = 0, mode = LsonNumberFormat.NumberFormatMode.DOWN)
            @LsonPath("state.coin")
            String coin;

            @LsonPath("img")
            String img;
        }
    }
}