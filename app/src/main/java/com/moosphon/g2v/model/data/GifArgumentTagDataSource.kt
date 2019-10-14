package com.moosphon.g2v.model.data

import android.content.Context
import com.google.gson.Gson
import com.moosphon.g2v.BuildConfig
import com.moosphon.g2v.model.Tag
import com.moosphon.g2v.util.loge
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * Load gif arguments config from local resource.
 */
object GifArgumentTagDataSource {

    private val json = "{\n" +
            "  \"tags\": [\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-742959fa80e0\",\n" +
            "      \"category\" : \"Frame rate\",\n" +
            "      \"displayName\" : \"20fps\",\n" +
            "      \"value\" : 20,\n" +
            "      \"color\" : \"#4768FD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-74718fu19113\",\n" +
            "      \"category\" : \"Frame rate\",\n" +
            "      \"displayName\" : \"30fps\",\n" +
            "      \"value\" : 30,\n" +
            "      \"color\" : \"#4768FD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-g8ag9g116666\",\n" +
            "      \"category\" : \"Frame rate\",\n" +
            "      \"displayName\" : \"60fps\",\n" +
            "      \"value\" : 60,\n" +
            "      \"color\" : \"#4768FD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-74284931ud11\",\n" +
            "      \"category\" : \"Aspect ratio\",\n" +
            "      \"displayName\" : \"默认\",\n" +
            "      \"value\" : 0,\n" +
            "      \"color\" : \"#574DDD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-742849owfof5\",\n" +
            "      \"category\" : \"Aspect ratio\",\n" +
            "      \"displayName\" : \"1:1\",\n" +
            "      \"value\" : 1,\n" +
            "      \"color\" : \"#574DDD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7430dagag389\",\n" +
            "      \"category\" : \"Aspect ratio\",\n" +
            "      \"displayName\" : \"3:4\",\n" +
            "      \"value\" : 0.75,\n" +
            "      \"color\" : \"#574DDD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7430sa3tu115\",\n" +
            "      \"category\" : \"Aspect ratio\",\n" +
            "      \"displayName\" : \"9:16\",\n" +
            "      \"value\" : 0.5625,\n" +
            "      \"color\" : \"#574DDD\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "\n" +
            "\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7432fuaga000\",\n" +
            "      \"category\" : \"Rotation\",\n" +
            "      \"displayName\" : \"0°\",\n" +
            "      \"value\" : 0,\n" +
            "      \"color\" : \"#94DD6B\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7432aatu0a11\",\n" +
            "      \"category\" : \"Rotation\",\n" +
            "      \"displayName\" : \"90°\",\n" +
            "      \"value\" : 90,\n" +
            "      \"color\" : \"#94DD6B\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7432aagag999\",\n" +
            "      \"category\" : \"Rotation\",\n" +
            "      \"displayName\" : \"180°\",\n" +
            "      \"value\" : 180,\n" +
            "      \"color\" : \"#94DD6B\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7432saf75731\",\n" +
            "      \"category\" : \"Rotation\",\n" +
            "      \"displayName\" : \"270°\",\n" +
            "      \"value\" : 270,\n" +
            "      \"color\" : \"#94DD6B\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "\n" +
            "\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7436sa989fa\",\n" +
            "      \"category\" : \"Speed\",\n" +
            "      \"displayName\" : \"0.5x\",\n" +
            "      \"value\" : 0.5,\n" +
            "      \"color\" : \"#FD9127\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7436a8s90fd\",\n" +
            "      \"category\" : \"Speed\",\n" +
            "      \"displayName\" : \"1x\",\n" +
            "      \"value\" : 1,\n" +
            "      \"color\" : \"#FD9127\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7436sif2930\",\n" +
            "      \"category\" : \"Speed\",\n" +
            "      \"displayName\" : \"2x\",\n" +
            "      \"value\" : 2,\n" +
            "      \"color\" : \"#FD9127\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "\n" +
            "\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7460r9uff11\",\n" +
            "      \"category\" : \"Resolution\",\n" +
            "      \"displayName\" : \"1x\",\n" +
            "      \"value\" : 1,\n" +
            "      \"color\" : \"#999999\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-74603989fj3\",\n" +
            "      \"category\" : \"Resolution\",\n" +
            "      \"displayName\" : \"0.75x\",\n" +
            "      \"value\" : 0.75,\n" +
            "      \"color\" : \"#999999\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\" : \"1446ffe1-aa35-4a05-9fd6-7460a9g3115\",\n" +
            "      \"category\" : \"Resolution\",\n" +
            "      \"displayName\" : \"0.5x\",\n" +
            "      \"value\" : 0.5,\n" +
            "      \"color\" : \"#999999\",\n" +
            "      \"fontColor\" : \"#202124\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"

    fun loadArgumentData(context: Context) : List<Tag> =
        parseJsonData(json).tags

    private fun getJsonFromAssets(context: Context) : String {
        val stringBuilder = StringBuilder()
        val assetManager = context.assets
        try {
            val bufferedReader = BufferedReader(
                InputStreamReader(
                    assetManager.open(BuildConfig.GIF_ARGUMENT_DATA_PATH), "utf-8"
                )
            )
            while (bufferedReader.readLine() != null) {
                stringBuilder.append(bufferedReader.readLine())
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    private fun parseJsonData(jsonString: String): TagList {
        //loge("获取的本地json数据：$jsonString")
        val gson = Gson()
        return gson.fromJson(jsonString, TagList::class.java)
    }
}

data class TagList(
    val tags: List<Tag>
)