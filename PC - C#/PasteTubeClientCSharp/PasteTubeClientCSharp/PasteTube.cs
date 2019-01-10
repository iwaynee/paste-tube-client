using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.IO;
using System.Windows.Forms;
using Newtonsoft.Json.Linq;

namespace PasteTubeClientCSharp
{
    class PasteTube
    {
        //public static string adress = "http://localhost:8080";
        public static string adress = "http://paste-tube.herokuapp.com";
        private string userid = string.Empty;

        public static string CreateUser()
        {
            // Create User first
            JObject response = PasteTube.MakeRequest("/CreateUser");
            string userid_temp = response["userid"].ToString();

            return userid_temp;
        }

        public PasteTube(string p_userid )
        {
            userid = p_userid;

            // Connect
            JObject response = PasteTube.MakeRequest("/Connect?mac=xyz&userid=" + userid);

            // MessageBox.Show(userid, "test", MessageBoxButtons.YesNo);
        }

        public JObject GetConnectedDevices()
        {
            // GetConnectedDevices
            JObject response = PasteTube.MakeRequest("/GetConnectedDevices?userid=" + userid);
            return response;
        }

        public JObject Copy( string data)
        {
            // Copy
            JObject response = PasteTube.MakeRequest("/Copy?userid=" + userid+"&data=" + data);
            return response;
        }

        public JObject Paste()
        {
            // Paste
            JObject response = PasteTube.MakeRequest("/Paste?userid=" + userid);
            return response;
        }

        private static JObject MakeRequest( string api_call)
        {
            string ret = string.Empty;

            var webRequest = WebRequest.Create(PasteTube.adress + api_call) as HttpWebRequest;
            if (webRequest != null)
            {
                webRequest.Method = "GET";
                webRequest.ServicePoint.Expect100Continue = false;
                webRequest.Timeout = 20000;
                webRequest.ContentType = "application/json";
            }
        
            HttpWebResponse resp = (HttpWebResponse)webRequest.GetResponse();
            Stream resStream = resp.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            ret = reader.ReadToEnd();

            JObject json = JObject.Parse(ret);

            return json;
        }
    }
}
