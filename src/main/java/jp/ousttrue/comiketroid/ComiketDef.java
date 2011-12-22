package jp.ousttrue.comiketroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Map;
import java.util.LinkedHashMap;
import android.os.Environment;
import android.util.Log;


class ComiketDef {
  private static final String TAG = "ComiketDef";

  enum Section {
    COMIKET("Comiket"),
    CUTINFO("cutInfo"),
    MAPTABLEINFO("mapTableInfo"),
    COMIKETDATE("ComiketDate"),
    COMIKETMAP("ComiketMap"),
    COMIKETAREA("ComiketArea"),
    COMIKETGENRE("ComiketGenre");

    String name;

    Section(String name){
      this.name=name;
    }

    public String toString(){
      return name;
    }

    static Section fromString(String name){
      for(Section s: Section.values()){
        if(s.toString().equals(name)){
          return s;
        }
      }
      return null;
    }
  }

  String name;
  Map<String, String> dateMap;
  Map<String, String> areaMap;
  Map<String, String> genreMap;

  ComiketDef(String name){
    this.name=name;
    this.dateMap=new LinkedHashMap<String, String>();
    this.areaMap=new LinkedHashMap<String, String>();
    this.genreMap=new LinkedHashMap<String, String>();

    BufferedReader reader=getReader();
    if(reader==null){
      return;
    }
    try{
      Section current=null;
      for(String line=reader.readLine(); 
          line!=null; 
          line=reader.readLine())
      {
        line=line.trim();
        if("".equals(line)){
          continue;
        }
        if(line.startsWith("#")){
          continue;
        }
        if(line.startsWith("*")){
          current=Section.fromString(line.substring(1));
          Log.d(TAG, "current: "+line);
          continue;
        }
        Log.d(TAG, line);

        switch(current){
          case COMIKET:
            break;

          case CUTINFO:
            break;

          case MAPTABLEINFO:
            break;

          case COMIKETDATE:
            {
              String[] s=line.split("\t");
              dateMap.put(s[3], 
                  String.format("%s/%s/%s(%s)", s[0], s[1], s[2], s[3]));
            }
            break;

          case COMIKETMAP:
            break;

          case COMIKETAREA:
            {
              String[] s=line.split("\t");
              areaMap.put(s[0], s[2]);
            }
            break;

          case COMIKETGENRE:
            {
              String[] s=line.split("\t", 2);
              genreMap.put(s[0],  s[1]);
            }
            break;

          default:
            break;
        }
      }
    }
    catch(java.io.IOException e){
      Log.e(TAG, e.getMessage());
      return;
    }

  }

  private BufferedReader getReader(){
    File sdcard=Environment.getExternalStorageDirectory();
    final File def=(new File(sdcard, 
          String.format("Comiket/%s/CDATA/%sDEF.txt",
            name, name)));
    try{
      return new BufferedReader(
        new InputStreamReader(new FileInputStream(def), "Shift_JIS"));
    }
    catch(java.io.FileNotFoundException e){
      Log.e(TAG, e.getMessage());
      return null;
    }
    catch(java.io.UnsupportedEncodingException e){
      Log.e(TAG, e.getMessage());
      return null;
    }
  }

  String getName(){
    return name;
  }

  String getDate(String weekday){
    return dateMap.get(weekday);
  }

  String getAreaName(String block){
    for(Map.Entry<String, String> e: areaMap.entrySet()) {
      if(e.getValue().contains(block)){
        return e.getKey();
      }
    }
    return null;
  }

  String getGenre(int genreCode){
    return genreMap.get(String.format("%d", genreCode));
  }
}

