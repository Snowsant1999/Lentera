package com.PPBM.lentera.features.auth



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

class LoginScreen {

    @Composable
    fun login(){

        //PARENTS
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier){

            //HEADER
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(412.dp)
                    .height(254.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(412.dp)
                        .height(52.dp)
                        .padding(start = 18.dp, end = 18.dp)
                ) {
                    // Child views.
                }

                Image(
                    painter = painterResource(id = R.drawable.lenteralogo),
                    contentDescription = "image description",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(154.dp)
                        .height(154.dp)
                )

                Text(
                    text = "Satu akses untuk kehidupan kampusmu.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF64748B),
                    ),
                    modifier = Modifier
                        .width(226.dp)
                        .height(15.dp)
                )
            }

            //BODY
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(412.dp)
                    .height(550.dp)
                    .padding(start = 24.dp, top = 28.dp, end = 24.dp)
            ) {
                //heading form login
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .width(364.dp)
                        .height(85.dp)
                ) {
                    Text(
                        text = "SELAMAT DATANG",
                        Modifier
                            .width(364.dp)
                            .height(35.dp),
                        style = TextStyle(
                            fontSize = 28.sp,
                            lineHeight = 35.sp,
                            fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                            fontWeight = FontWeight(800),
                            color = Color(0xFF173B57),
                        ),
                    )

                    Text(
                        text = "Masuk untuk mengakses layanan pengaduan dan peluang magang di LENTERA.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF64748B),
                        ),
                        modifier = Modifier
                            .width(364.dp)
                            .height(42.dp)
                    )
                }

                //badannya form login
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .shadow(elevation = 16.dp, spotColor = Color(0x09173B57), ambientColor = Color(0x09173B57))
                        .width(364.dp)
                        .height(353.dp)
                        .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 20.dp))
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
                ) {
                    //usn
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .width(324.dp)
                            .height(98.dp)
                    ) {
                        Text(
                            text = "NIM / Email Kampus",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFF173B57),
                            ),
                            modifier = Modifier
                                .width(324.dp)
                                .height(15.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(width = 1.dp, color = Color(0xFFDCE6EE), shape = RoundedCornerShape(size = 14.dp))
                                .width(324.dp)
                                .height(52.dp)
                                .background(color = Color(0xFFEFF5FD), shape = RoundedCornerShape(size = 14.dp))
                                .padding(start = 14.dp, end = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iconamoon_profile_circle_thin),
                                contentDescription = "image description",
                                contentScale = ContentScale.None,
                                modifier = Modifier
                                    .padding(1.dp)
                                    .width(20.dp)
                                    .height(20.dp)
                            )

                            Text(
                                text = "Masukkan NIM atau email kampus",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF64748B),
                                ),
                                modifier = Modifier
                                    .width(272.dp)
                                    .height(18.dp)
                            )
                        }
                    }

                    //passsword
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .width(324.dp)
                            .height(75.dp)
                    ) {
                        //text
                        Text(
                            text = "Password",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFF173B57),
                            ),
                            modifier = Modifier
                                .width(324.dp)
                                .height(15.dp)
                        )

                        //input
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(width = 1.dp, color = Color(0xFFDCE6EE), shape = RoundedCornerShape(size = 14.dp))
                                .width(324.dp)
                                .height(52.dp)
                                .background(color = Color(0xFFEFF5FD), shape = RoundedCornerShape(size = 14.dp))
                                .padding(start = 14.dp, end = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lock_keyhole),
                                contentDescription = "image description",
                                contentScale = ContentScale.None,
                                modifier = Modifier
                                    .padding(1.dp)
                                    .width(20.dp)
                                    .height(20.dp)
                            )

                            //placeholder
                            Text(
                                text = "Masukkan password",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF64748B),
                                ),
                                modifier = Modifier
                                    .width(222.dp)
                                    .height(18.dp)
                            )

                            //show pass
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(44.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(44.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.eye_off),
                                        contentDescription = "image description",
                                        contentScale = ContentScale.None,
                                        modifier = Modifier
                                            .padding(1.dp)
                                            .width(20.dp)
                                            .height(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    //lupa password
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(324.dp)
                            .height(24.dp)
                    ) {
                        Text(
                            text = "Lupa password?",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF23496E),
                            ),
                            modifier = Modifier
                                .width(96.dp)
                                .height(15.dp)
                        )
                    }

                    //button login
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(324.dp)
                            .height(56.dp)
                            .background(color = Color(0xFF23496E), shape = RoundedCornerShape(size = 16.dp))
                    ) {
                        Text(
                            text = "Masuk",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFFFFFFFF),
                            ),
                            modifier = Modifier
                                .width(45.dp)
                                .height(18.dp)
                        )
                    }
                }

                //keterangan acc
                Text(
                    text = "Akun dikelola oleh kampus.\nHubungi admin kampus jika belum memiliki akses.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(364.dp)
                        .height(36.dp)
                )
            }

            //FOOTER
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(412.dp)
                    .height(83.dp)
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "LENTERA · Versi 1.0.0",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(412.dp)
                        .height(15.dp)
                )
            }
        }
    }


    @Preview(showBackground = true, widthDp = 412, heightDp = 917)
    @Composable
    fun loginPreview(){
        login()

    }
}