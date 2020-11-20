package top.colter.myplugin

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import top.colter.myplugin.PluginData.runPath
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import kotlin.math.ceil


/**
 * 解析emojiJson 获取图片封装进map
 */
fun getEmoji(emojiJson: JSONArray, emoji: MutableMap<String, java.awt.Image>){
    for (emojiItem in emojiJson){
        var em = emojiItem as JSONObject
        emoji[em.getString("emoji_name")] = ImageIO.read(URL(em.getString("url")))
    }
}

/**
 * get请求
 */
fun httpGET(url: String): JSONObject {

    var link = URL(url)
    var response = link.readText()

    return JSON.parseObject(response)
}

/**
 * 生成粉丝数图片
 */
suspend fun getFanImg(uid:Int, name: String): BufferedImage{

    val followNum = httpGET(PluginData.followNumApi + uid).getJSONObject("data").getInteger("follower").toInt()

    var bg = ImageIO.read(File("$runPath/bg/$name"+"fan.png"))
    var bi = BufferedImage(1920, 426, BufferedImage.TYPE_INT_RGB)
    var g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景

    g2.font = Font("汉仪汉黑W", Font.BOLD, 85)
    g2.color = Color(87, 87, 87)
    g2.drawString(followNum.toString(), 870, 270)

    g2.font = Font("微软雅黑", Font.BOLD, 60)
    g2.color = Color(148, 147, 147)
    var timestamp :Long = System.currentTimeMillis()
    g2.drawString(SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(timestamp), 570, 345)

    return bi

}

/**
 * 生成每日总结图片
 */
suspend fun getSummaryImg(timestamp:Long,info: MutableList<MutableMap<String, Int>>): BufferedImage{

    val time = SimpleDateFormat("MM.dd").format(timestamp)

    var bg = ImageIO.read(File("$runPath/bg/summary.png"))
    var bi = BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    var g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景

    g2.font = Font("微软雅黑", Font.BOLD, 68)
    g2.color = Color(52, 52, 52)
    g2.drawString(time, 690, 113)

    g2.font = Font("汉仪汉黑W", Font.BOLD, 70)
    g2.color = Color(87, 87, 87)

    var y = 280
    for (member in info){
        g2.drawString(member["fan"].toString(), 1050, y)
        g2.drawString(member["riseFan"].toString(), 1570, y)
        y += 105
        g2.drawString(member["guard"].toString(), 1050, y)
        g2.drawString(member["riseGuard"].toString(), 1570, y)
        y += 200
    }

    //把图片写入文件
    try{
        ImageIO.write(bi, "JPEG", FileOutputStream("$runPath/bg/summary/${SimpleDateFormat("MMdd").format(timestamp)}.jpg"))
    }catch (e:Exception){
        PluginMain.logger.error("储存图片失败")
    }

    return bi
}

/**
 * 简单参数测试函数
 */
suspend fun getMsgImg(msg: String): BufferedImage {
    var unixTimestamp : Long= System.currentTimeMillis()/1000
    return getMsgImg(msg, unixTimestamp, "bell", 66666, "99999999999999999", null, null)
}

/**
 * 根据内容绘制图片
 */
suspend fun getMsgImg(msg: String, unixTimestamp: Long, name: String, followNum: Int, dynamicId: String, emojiList: MutableMap<String, java.awt.Image>?, imgList: MutableList<String>?): BufferedImage {
//    PluginData.dynamicCount++

    //统计最终图片所需的高度
    var height = 0
    //获取一张基础背景图
    var bg = ImageIO.read(File("$runPath/bg/$name.png"))

    //构建一张画布以供裁剪
    var bi = BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    var g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景

    //最终图片片段列表
    var biList = mutableListOf<BufferedImage>()

    //裁剪图片头 并写入粉丝数 时间信息
    var topBi = bi.getSubimage(0, 0, 1920, 370);
    height += 370
    biList.add(topBi)
    var topG2 : Graphics2D = topBi.graphics as Graphics2D
    topG2.font = Font("汉仪汉黑W", Font.PLAIN, 80)
    topG2.color = Color(87, 87, 87)
    topG2.drawString(followNum.toString(), 770, 265)

    topG2.font = Font("微软雅黑", Font.BOLD, 45)
    topG2.color = Color(148, 147, 147)
    var timestamp :Long = unixTimestamp*1000
    topG2.drawString(SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(timestamp), 530, 330)

    //加一条空白区域
    height += 70
    biList.add(bi.getSubimage(0, 400, 1920, 70))

    //处理动态内容
    var stringList = mutableListOf<String>()
    var msgIndex = 0
    var msgEnd = 0
    var msgText = msg

    val textBi =  BufferedImage(1920, 70, BufferedImage.TYPE_INT_RGB)
    val textG2 : Graphics2D = textBi.graphics as Graphics2D
    textG2.font = Font("微软雅黑", Font.PLAIN, 60)

    var l = 0
    var tin = false
    var start = 0
    var fl = 0

    for((i,c) in msgText.withIndex()){

        if (c == '\n'){
            stringList.add(msgText.substring(start, i))
            start = i+1
            l = 0
        }

        if (c == '['){
            tin = true
        }
        if (!tin){
            fl = textG2.font.getStringBounds(c.toString(), textG2.fontRenderContext).width.toInt()
        }
        if (c == ']'){
            tin = false
            l += 65
        }

        if (l+fl>1680){
            stringList.add(msgText.substring(start, i+1))
            start = i+1
            fl = 0
            l = 0
        }else{
            l += fl
            fl = 0
        }
    }
    stringList.add(msgText.substring(start))


    //处理换行
//    while (true){
//        msgIndex = msgText.indexOf('\n')
//        var tempText = msgText
//        if (msgIndex!=-1){
//            tempText = msgText.substring(0, msgIndex)
//        }
//
//        var count = 0
//        var tin = false
//        var ei = 0
//        var el = 0
//        var ec = 0
//        var i = 0
//        //限制每行29个字符
//        while(true){
//
//            if (tempText[i] == '['){
//                ei = i
//                ec++
//                tin = true
//            }
//            if (tempText[i] == ']'){
//                el += i - ei + 1
//                tin = false
//            }
//            if (!tin){
//                count++
//            }
//            if (count==29){
//                stringList.add(tempText.substring(0, 29 + el))
//                tempText = tempText.substring(29 + el)
//                el = 0
//                count = 0
//                i = 0
//                ec = 0
//            }
//            if (tempText.length-el+ec<29){
//                break
//            }
//            i++
//        }
//
//        stringList.add(tempText)
//        if (msgIndex!=-1){
//            msgText = msgText.substring(msgIndex + 1)
//        }else{
//            break
//        }
//    }


    //构建动态内容图片片段
    for (index in 1..stringList.size){
        val centerBi =  BufferedImage(1920, 70, BufferedImage.TYPE_INT_RGB)
        val centerG2 : Graphics2D = centerBi.graphics as Graphics2D
        centerG2.drawImage(bi.getSubimage(0, 470, 1920, 70), 0, 0, null)

        centerG2.font = Font("微软雅黑", Font.PLAIN, 60)
        centerG2.color = Color(87, 87, 87)

        height += 70
        biList.add(centerBi)

        var start = 0
        var end = 0
        var text = stringList[index - 1]
        var x = 100
        //解析b站表情 [tv_doge]
        while (text.indexOf('[')!=-1){
            start = text.indexOf('[')
            end = text.indexOf(']')+1
            val tempText = text.substring(0, start)
            val tempSimp = text.substring(start, end)
            text = text.substring(end)

            if (start != 0){
                centerG2.drawString(tempText, x, 50)

                //计算字符串像素长度 用于绘制表情
                x += centerG2.font.getStringBounds(tempText, centerG2.fontRenderContext).width.toInt()
            }
            try{
                val emoji = emojiList?.get(tempSimp)
                val reEmoji = emoji?.getScaledInstance(65, 65, java.awt.Image.SCALE_DEFAULT)
                centerG2.drawImage(reEmoji, x, 0, null)
            }catch (e: Exception){

            }
            x+=65
        }

        centerG2.drawString(text, x, 50)
    }

    try{
        //如动态有图片则添加图片
        if (imgList != null && imgList.size!=0){
            for (imgSrc in imgList){
                var img = ImageIO.read(URL(imgSrc))
                var reHeight = (img.height*1720.0)/img.width
                var reImg = img.getScaledInstance(1720, reHeight.toInt(), java.awt.Image.SCALE_DEFAULT)
                var row : Int = ceil(reHeight / 70.0).toInt()
                var imgBi = BufferedImage(1920, row * 70, BufferedImage.TYPE_INT_RGB)
                var imgG2 = imgBi.graphics as Graphics2D
                for (index in 1..row) {
                    imgG2.drawImage(bi.getSubimage(0, 470, 1920, 70), 0, (index - 1) * 70, null)
                }
                imgG2.drawImage(reImg, 100, 30, null)
                height += row*70
                biList.add(imgBi)
            }
        }
    }catch (e:Exception){
        PluginMain.logger.error("绘制图片失败")
    }


    //内容底部留空
    height += 70
    biList.add(bi.getSubimage(0, 400, 1920, 70))

    //构建底部图片片段 写入动态ID
    var bottomBi = bi.getSubimage(0, 995, 1920, 85)
    var bottomG2 : Graphics2D = bottomBi.graphics as Graphics2D
    height += 85
    biList.add(bottomBi)
    bottomG2.font = Font("微软雅黑", Font.BOLD, 45)
    bottomG2.color = Color(217, 217, 217)
    if (dynamicId.length<10){
        bottomG2.drawString("直播ID:$dynamicId", 80, 39)
    }else{
        bottomG2.drawString("动态ID:$dynamicId", 80, 39)
    }


    //构建最终图片
    var endBi = BufferedImage(1920, height, BufferedImage.TYPE_INT_RGB)
    var endG2 : Graphics2D = endBi.graphics as Graphics2D
    var preY = 0
    for (bi in biList){
        endG2.drawImage(bi, 0, preY, null)
        preY += bi.height
    }

    //把图片写入文件
    try{
        ImageIO.write(endBi, "JPEG", FileOutputStream("$runPath/bg/history/$dynamicId.jpg"))
    }catch (e:Exception){
        PluginMain.logger.error("储存图片失败")
    }


    return endBi
}

