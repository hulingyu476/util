private boolean isWhiteListApp(String packagename){
    ArrayList<String> whiteListApp = new ArrayList<String>();
    try{
        BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream("/system/etc/WhiteListApp.conf")));
        String line ="";
        while ((line = br.readLine()) != null){
            whiteListApp.add(line);
        }
        br.close();
    }catch(java.io.FileNotFoundException ex){
        return false;
    }catch(java.io.IOException ex){
        return false;
    }
    Iterator<String> it = whiteListApp.iterator();
    while (it.hasNext()) {
        String whitelistItem = it.next();
        if (whitelistItem.equals(packagename)) {
            return true;
        }
    }
    return false;
}