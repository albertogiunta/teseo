package com.jaus.albertogiunta.teseo

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jaus.albertogiunta.teseo.data.AreaViewedFromAUser
import com.jaus.albertogiunta.teseo.utils.Unmarshaler
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun Marshaling_CorrectAreaUnmarshaling() {
        val area: AreaViewedFromAUser = Unmarshaler.unmarshalArea(BufferedReader(InputStreamReader(getAppContext().resources.openRawResource(R.raw.area))).lines().collect(Collectors.joining("\n")))
        assertTrue(area.id == 42)
        assertTrue(area.rooms.size == 15)
    }

    fun getAppContext(): Context = InstrumentationRegistry.getTargetContext()
}
