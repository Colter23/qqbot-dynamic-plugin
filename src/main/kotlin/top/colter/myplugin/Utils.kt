package top.colter.myplugin

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import top.colter.myplugin.PluginConfig.runPath
import top.colter.myplugin.translate.TransApi
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import kotlin.math.ceil


/**
 * 解析emojiJson 获取图片封装进map
 */
fun getEmoji(emojiJson: JSONArray, emoji: MutableMap<String, java.awt.Image>){
    for (emojiItem in emojiJson){
        val em = emojiItem as JSONObject
        emoji[em.getString("emoji_name")] = ImageIO.read(URL(em.getString("url")))
    }
}

/**
 * get请求
 */
fun httpGET(url: String): JSONObject {

    val link = URL(url)
    val response = link.readText()

    return JSON.parseObject(response)
}

/**
 * 带cookie的get请求
 */
fun httpGetWithCookie(url: String): JSONObject {

    val link = URL(url)
    val connection = link.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connectTimeout = 300000

//    connection.setRequestProperty("charset", "utf-8")
    connection.setRequestProperty("cookie", PluginConfig.cookie)

    try {
        val inputStream: DataInputStream = DataInputStream(connection.inputStream)
        val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
        val output: String = reader.readLine()

        return JSON.parseObject(output)

    } catch (exception: Exception) {
        throw Exception(exception.message)
    }
}

/**
 * 生成粉丝数图片
 */
suspend fun getFanImg(uid: Int, name: String): BufferedImage{

    val followNum = httpGET(PluginConfig.followNumApi + uid).getJSONObject("data").getInteger("follower").toInt()

    var bg = ImageIO.read(File("$runPath/bg/$name" + "fan.png"))
    var bi = BufferedImage(1920, 426, BufferedImage.TYPE_INT_RGB)
    var g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

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
suspend fun getSummaryImg(timestamp: Long, info: MutableList<MutableMap<String, Int>>): BufferedImage{

    val time = SimpleDateFormat("MM.dd").format(timestamp)

    var bg = ImageIO.read(File("$runPath/bg/summary.png"))
    var bi = BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    var g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
//    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
    }catch (e: Exception){
        PluginMain.logger.error("储存图片失败")
    }
    return bi
}

fun generateImg(uid: String, name: String, face: String, pendant: String){
    val bg = ImageIO.read(File("$runPath/bg/template.png"))
    val faceImg = ImageIO.read(URL(face))
    val pendantImg = ImageIO.read(URL(pendant))

    val bi = BufferedImage(bg.width, bg.height, BufferedImage.TYPE_4BYTE_ABGR)
    val g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
    g2.font = Font("汉仪汉黑W", Font.PLAIN, 135)
    g2.color = Color(61, 61, 61)
    g2.drawString(name, 480, 200)

//    val reImg = faceImg.getScaledInstance(195, 195, java.awt.Image.SCALE_DEFAULT)
    val reImg = scaleByPercentage(faceImg,195,195)

    val faceBi = BufferedImage(195, 195, BufferedImage.TYPE_4BYTE_ABGR)
    val faceG2 : Graphics2D = faceBi.graphics as Graphics2D
    faceG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//    val shape = Ellipse2D.Double(border, border, width - border * 2, width - border * 2)
    val shape = Ellipse2D.Double(0.0, 0.0, 195.0, 195.0)
//    faceG2.fill(Rectangle(195, 195))
    faceG2.clip = shape
    faceG2.drawImage(reImg, 0, 0, null)
//    faceG2.dispose();

    g2.drawImage(faceBi, 200, 100, null)

    val rePendant = scaleByPercentage(pendantImg,330,330)
    g2.drawImage(rePendant, 130, 40, null)

    //把图片写入文件
    try{
        ImageIO.write(bi, "PNG", FileOutputStream("$runPath/bg/$uid.png"))
    }catch (e: Exception){
        PluginMain.logger.error("储存图片失败")
    }
}

/**
 * 缩放图片
 */
fun scaleByPercentage(inputImage: BufferedImage, newWidth: Int, newHeight: Int): BufferedImage? {
    // 获取原始图像透明度类型
    try {
        val type = inputImage.colorModel.transparency
        val width = inputImage.width
        val height = inputImage.height
        // 开启抗锯齿
        val renderingHints = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        // 使用高质量压缩
        renderingHints[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY
        val img = BufferedImage(newWidth, newHeight, type)
        val graphics2d = img.createGraphics()
        graphics2d.setRenderingHints(renderingHints)
        graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null)
        graphics2d.dispose()
        return img
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return null
}


/**
 * 简单参数测试函数
 */
suspend fun getMsgImg(msg: String): BufferedImage {
    val unixTimestamp : Long= System.currentTimeMillis()/1000
    return getMsgImg(msg, unixTimestamp, "487550002", "99999999999999999", null, null)
}

/**
 * 根据内容绘制图片
 */
suspend fun getMsgImg(souMsg: String, unixTimestamp: Long, uid: String, dynamicId: String, emojiList: MutableMap<String, java.awt.Image>?, imgList: MutableList<String>?): BufferedImage {
//    PluginData.dynamicCount++

//    println(souMsg)
    var msg = souMsg
    //文本翻译
    if (PluginConfig.SECURITY_KEY!=""){
        try {
            val api = TransApi(PluginConfig.APP_ID, PluginConfig.SECURITY_KEY)
            val resMsg = JSON.parseObject(api.getTransResult(msg, "auto", "zh"))
            if (resMsg.getString("from")!="zh") {
                msg += "\n\n翻译: \n"
                for (item in resMsg.getJSONArray("trans_result")){
                    msg+=(item as JSONObject).getString("dst")
                    msg+="\n"
                }
            }
        }catch (e: Exception){
            PluginMain.logger.error("Baidu translation failure! 百度翻译失败!")
        }
    }else{
        PluginMain.logger.error("Baidu translation API not configured! 未配置百度翻译API")
    }


    //统计最终图片所需的高度
    var height = 0
    //获取一张基础背景图
    val bg = ImageIO.read(File("$runPath/bg/$uid.png"))

    //构建一张画布以供裁剪
    val bi = BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    val g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

    //最终图片片段列表
    val biList = mutableListOf<BufferedImage>()

    //裁剪图片头 并写入粉丝数 时间信息
    val topBi = bi.getSubimage(0, 0, 1920, 370);
    height += 370
    biList.add(topBi)
    val topG2 : Graphics2D = topBi.graphics as Graphics2D
    topG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
//    topG2.font = Font("汉仪汉黑W", Font.PLAIN, 80)
//    topG2.color = Color(87, 87, 87)
//    topG2.drawString(followNum.toString(), 770, 265)

    topG2.font = Font("微软雅黑", Font.BOLD, 70)
    topG2.color = Color(148, 147, 147)
    val timestamp :Long = unixTimestamp*1000
    topG2.drawString(SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(timestamp), 510, 300)

    //加一条空白区域
    height += 70
    biList.add(bi.getSubimage(0, 400, 1920, 70))

    //处理动态内容
    val stringList = mutableListOf<String>()
    var msgIndex = 0
    var msgEnd = 0
    val msgText = msg

    val textBi =  BufferedImage(1920, 70, BufferedImage.TYPE_INT_RGB)
    val textG2 : Graphics2D = textBi.graphics as Graphics2D
    textG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
    textG2.font = Font("微软雅黑", Font.PLAIN, 60)

    // 文字换行处理
    var l = 0
    var tin = false
    var start = 0
    var fl = 0
    for((i, c) in msgText.withIndex()){
        if (c == '\n'){
            stringList.add(msgText.substring(start, i))
            start = i+1
            l = 0
        }
        if (c == '[') tin = true
        if (!tin) fl = textG2.font.getStringBounds(c.toString(), textG2.fontRenderContext).width.toInt()
        if (c == ']'){
            tin = false
            l += 65
        }
        if (l+fl>1680){
            stringList.add(msgText.substring(start, i + 1))
            start = i+1
            fl = 0
            l = 0
        }else{
            l += fl
            fl = 0
        }
    }
    stringList.add(msgText.substring(start))

    //构建动态内容图片片段
    for (index in 1..stringList.size){
        val centerBi =  BufferedImage(1920, 70, BufferedImage.TYPE_INT_RGB)
        val centerG2 : Graphics2D = centerBi.graphics as Graphics2D
        centerG2.drawImage(bi.getSubimage(0, 470, 1920, 70), 0, 0, null)
        centerG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

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
                val img = ImageIO.read(URL(imgSrc))
                val reHeight = (img.height*1720.0)/img.width
                val reImg = img.getScaledInstance(1720, reHeight.toInt(), java.awt.Image.SCALE_DEFAULT)
                val row : Int = ceil(reHeight / 70.0).toInt()
                val imgBi = BufferedImage(1920, row * 70, BufferedImage.TYPE_INT_RGB)
                val imgG2 = imgBi.graphics as Graphics2D
                for (index in 1..row) {
                    imgG2.drawImage(bi.getSubimage(0, 470, 1920, 70), 0, (index - 1) * 70, null)
                }
                imgG2.drawImage(reImg, 100, 30, null)
                height += row*70
                biList.add(imgBi)
            }
        }
    }catch (e: Exception){
        PluginMain.logger.error("绘制图片失败")
    }

    //内容底部留空
    height += 70
    biList.add(bi.getSubimage(0, 400, 1920, 70))

    //构建底部图片片段 写入动态ID
    val bottomBi = bi.getSubimage(0, 995, 1920, 85)
    val bottomG2 : Graphics2D = bottomBi.graphics as Graphics2D
    bottomG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
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
    val endBi = BufferedImage(1920, height, BufferedImage.TYPE_INT_RGB)
    val endG2 : Graphics2D = endBi.graphics as Graphics2D
    var preY = 0
    for (bi in biList){
        endG2.drawImage(bi, 0, preY, null)
        preY += bi.height
    }

    //把图片写入文件
    try{
        ImageIO.write(endBi, "JPEG", FileOutputStream("$runPath/bg/history/$dynamicId.jpg"))
    }catch (e: Exception){
        PluginMain.logger.error("储存图片失败")
    }
    return endBi
}

