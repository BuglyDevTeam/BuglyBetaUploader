package com.tencent.bugly.plugin


public class HttpURLConnectionUtil {
    public static final String HTTPMETHOD_GET = "GET"
    public static final String HTTPMETHOD_POST = "POST"
    public static final String HTTPMETHOD_PUT = "PUT"

    private URL url;
    private String httpMethod;
    private HttpURLConnection conn;
    private final String BOUNDARY =  "---------betaUploader"; // 定义数据分隔线
    private Map<String, String> textParams = new HashMap<String, String>();
    private Map<String, File> fileParams = new HashMap<String, File>();
    private DataOutputStream ds;

    public HttpURLConnectionUtil(String url, String httpMethod) throws Exception {
        this.url = new URL(url);
        this.httpMethod = httpMethod;
    }

    /**
     * 增加一个普通字符串数据到form表单数据中
     * @param name
     * @param value
     */
    public void addTextParameter(String name, String value) {
        textParams.put(name, value);
    }

    /**
     * 增加一个文件到form表单数据中
     * @param name
     * @param value
     */
    public void addFileParameter(String name, File value) {
        fileParams.put(name, value);
    }

    /**
     * 清空所有已添加的form表单数据
     */
    public void clearAllParameters() {
        textParams.clear();
        fileParams.clear();
    }

    /**
     * 初始化连接
     * @throws Exception
     */
    private void initConnection() throws Exception{
        conn = (HttpURLConnection) this.url.openConnection();
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(10000); //连接超时为10秒
        conn.setRequestMethod(httpMethod);
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);

    }

    public byte[] post() throws Exception {
        initConnection();
        try {
            conn.connect();
        } catch (SocketTimeoutException e) {
            throw new RuntimeException();
        }

        ds = new DataOutputStream(conn.getOutputStream());
        writeStringParams()
        writeFileParams();
        paramsEnd();
        ds.flush();
        ds.close();
        InputStream inputStream = conn.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = inputStream.read()) != -1) {
            out.write(b);
        }
        conn.disconnect();
        return out.toByteArray();
    }

    private void writeStringParams() throws Exception {
        Set<String> keySet = textParams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String name = it.next();
            String value = textParams.get(name);
            if (ds != null && value != null ) {
                ds.writeBytes("--" + BOUNDARY + "\r\n");
                ds.writeBytes("Content-Disposition: form-data; name=\"" + name
                        + "\"\r\n");
                ds.writeBytes("\r\n");
                ds.write(value.getBytes("UTF-8"));
                ds.writeBytes("\r\n");
            }
        }
    }


    /**
     * 文件数据
     * @throws Exception
     */
    private void writeFileParams() throws Exception {
        Set<String> keySet = fileParams.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String name = it.next();
            File value = fileParams.get(name);
            ds.writeBytes("--" + BOUNDARY + "\r\n");
            ds.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\"; filename=\"" + value.getName() + "\"\r\n");
            ds.writeBytes("Content-Type:application/octet-stream" + "\r\n");
            ds.writeBytes("\r\n");
            ds.write(getBytes(value));
            ds.writeBytes("\r\n");
        }
    }

    /**
     * 把文件转换成字节数组
     * @param file
     * @return
     * @throws Exception
     */
    private byte[] getBytes(File file) throws Exception {
        DataInputStream inputStream = new DataInputStream(new FileInputStream(file))
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bufferOut = new byte[1024];
        int bytes = 0;
        while ((bytes = inputStream.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        inputStream.close()
        return out.toByteArray();
    }

    /**
     * 添加结尾数据
     * @throws Exception
     */
    private void paramsEnd() throws Exception {
        ds.writeBytes("--" + BOUNDARY + "--" + "\r\n");
        ds.writeBytes("\r\n");
    }

    /**
     * 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
     * @param value
     * @return
     * @throws Exception
     */
    private String encode(String value) throws Exception{
        return URLEncoder.encode(value, "UTF-8");
    }


    /**
     * read buffer from inputstream
     * @param input
     * @return
     */
    public String readBufferFromStream(InputStream input) {
        if (input == null)
            return null;
        String charSet = "UTF-8";
        BufferedInputStream buffer = new BufferedInputStream(input);
        ByteArrayOutputStream baos = null;
        String str = null;
        try {
            baos = new ByteArrayOutputStream();

            byte[] byteChunk = new byte[1024 * 16];
            int len = -1;
            while ((len = buffer.read(byteChunk)) > -1) {
                baos.write(byteChunk, 0, len);
            }
            baos.flush();
            byte[] bytes = baos.toByteArray();
            str = new String(bytes, charSet);
            if (baos != null) {
                baos.close();
                baos = null;
            }
        } catch (IOException e) {
            println("readBufferFromStream error", e);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (Exception e) {
                println("readBufferFromStream error!", e);
            }
        }
        return str;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
}
