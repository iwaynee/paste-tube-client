using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Threading;
using QRCoder;
using Newtonsoft.Json.Linq;
using System.Windows.Forms;

namespace PasteTubeClientCSharp
{
    public partial class form1 : MetroFramework.Forms.MetroForm
    {
        public form1()
        {
            InitializeComponent();
        }

        private void metroButton1_Click(object sender, EventArgs e)
        {
            this.metroButton1.Hide();
            this.metroButton2.Hide();

            string userid = PasteTube.CreateUser();

            // Create QR Code
            QRCodeGenerator qrGenerator = new QRCodeGenerator();
            QRCodeData qrCodeData = qrGenerator.CreateQrCode(userid, QRCodeGenerator.ECCLevel.Q);
            QRCode qrCode = new QRCode(qrCodeData);
            Bitmap qrCodeImage = qrCode.GetGraphic(20);
            this.pictureBox1.Image = qrCodeImage;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.Zoom;
            this.pictureBox1.Show();

            //var t = new Thread(() => MyThread(userid));
            //t.Start();

            MyThread(userid);

            this.Text = "PasteTube : Running!";

        }

        private void metroButton2_Click(object sender, EventArgs e)
        {
            this.metroButton1.Hide();
            this.metroButton2.Hide();

            string userid = "asdfasdf";

            var t = new Thread(() => MyThread(userid));
            t.Start();


            this.Text = "PasteTube : Running!";
        }

        private static void MyThread(string userid)
        {
            Console.WriteLine(userid);
            PasteTube client = new PasteTube(userid);

            string old = string.Empty;

            while (true)
            {

                JObject response = client.Paste();
                string new_online = response["data"].ToString();
                var new_offline = Clipboard.GetText();


                if (new_offline != old)
                {
                    old = new_offline;
                    Console.WriteLine(old);
                    client.Copy(old);
                }
                else if (new_online != old)
                {
                    old = new_online;
                    Console.WriteLine(old);
                    Clipboard.SetText(old);
           
                }

                Thread.Sleep(100);
            }
        }
    }
}
