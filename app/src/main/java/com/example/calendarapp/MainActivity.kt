package com.example.calendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calendarapp.ui.theme.CalendarAppTheme
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalendarScreen()
        }
    }
}

@Composable
fun CalendarScreen() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    SimpleCalendar(
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it }
    )
}

@Composable
fun SimpleCalendar(
    //  현재 사용자가 선택한 날짜
    selectedDate: LocalDate?,
    //  날짜를 클릭했을 때 선택된 날짜를 상위로 전달하는 콜백함수
    onDateSelected: (LocalDate) -> Unit
) {
    //  현재 보여주고 있는 달 2025-07
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    //  해당 월의 총 일수(30,31)
    val daysInMonth = currentMonth.lengthOfMonth()

    //  해당 월에 1일이 무슨 요일인지 계산
    //  월(1) ~ 일(7)
    //  달력은 일요일부터 시작하기 때문에 % 7을 함
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
    //buildList는 반복과정에서 조건을 이용할때 사용합니다.
    val dates = buildList<LocalDate?> {
        repeat(firstDayOfWeek) { add(null) } //앞에 빈칸 채움
        for (day in 1..daysInMonth) {
            add(currentMonth.atDay(day)) // 1일부터 마지막 날까지 날짜 채움
        }

        //  마지막주에 몇 칸 채워졌는지 확인
        val remainder = size % 7
        if (remainder != 0) {
            // 부족한 칸 개수 계산
            repeat(7 - remainder) { add(null) } // 마지막 줄 빈칸 채움
        }
    }
    println("test:$dates")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "이전 달"
                )
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "다음 달"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 한주는 7일 이기에 7로 나눔
        dates.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isSelected = date != null && date == selectedDate

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f) // Box 셀 하나를 정사각형으로 만들어 달력의 줄 간격을 일정하게 유지
                            .padding(4.dp)
                            .clip(CircleShape) // 원형 배경
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = date != null) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            color = when {
                                isSelected -> Color.White
                                else -> Color.Black
                            },
                        )
                    }
                }
            }
        }
    }
}