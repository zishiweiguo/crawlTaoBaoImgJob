#coding=utf-8

# 爬取天猫店铺宝贝图片

# 正则
import re
# 网络交互
import requests
# 操作系统功能
import os
import shutil
import json
import sys
import zipfile


# 获取橱窗图片
def getWindowInfo(self, _url, _position, _regX,p):
    # 要爬的网址
    url = _url
    urls = url.split(",")
    url = urls[0]
    name = urls[1].replace("\n","")
    if isinstance(name, unicode):
        name = name.encode("gbk")
    else:
        name = name.decode("utf-8").encode("gbk")
    # 本地地址
    position = _position
    # 获取网页源代码
    html = requests.get(url).text

    # 正则
    regX = _regX

    #https://img.alicdn.com/bao/uploaded/i1/TB1ZtbrMXXXXXciXXXXXXXXXXXX_!!0-item_pic.jpg_430x430q90.jpg

    title = re.findall("<h1 data-spm=\"1000983\">(.*?)</h1>", html, re.S)
    s = ""
    for t in title:
        s = t.replace("\r", "").replace("\n", "").replace("\t", "").lstrip()
    # title = str(title[0].decode('utf-8')).rstrip()

    if not os.path.isdir(position + name):
        os.makedirs(position + name)

    fp = open(position + name + "/name.txt", 'wb')
    fp.write(s.encode("gbk"))
    # s = s.encode("gbk")
    # p = p.encode("gbk")
    position = position + name + "/" + p

    pic_url = re.findall(regX, html, re.S)

    # 如果文件夹不存在，则创建一个文件夹
    if not os.path.isdir(position):
        os.makedirs(position)
    else:
        os.removedirs(position)

    i = 1
    for each in pic_url:
        x = each.replace("\"", "")
        if ("<span>" in each):
           y = x.split(")")[0].replace("60", "430").replace("40", "430")
           n = x.split("<span>")[1]
        else:
            y = x.replace("60","430").replace("40","430")
            n = u""
        print ("https:"+y)

        if(p == "right"):
            urlName = re.findall("<span>(.*?)$", each, re.S)
            urlName = cusDecode(urlName[0])
        else:
            urlName = str(i + 1)
        pic = requests.get("https:"+y).content
        fp = open(position + "/" + urlName + "_.jpg", 'wb')
        fp.write(pic)
        fp.close()
        i += 1
    print("共爬取" + str(i) + "张图片")

def cusDecode(v):
    if isinstance(v, unicode):
        v = v.encode("gbk")
    else:
        v = v.decode("utf-8").encode("gbk")
    return v

# 获取详情图片
def getDetailInfo(self, _url, _position, _regX,p):
    tmall_exp = 'Setup\(([\s\S]+?)\)'

    # 要爬的网址
    url = _url
    urls = url.split(",")
    url = urls[0]
    name = urls[1].replace("\n","")
    if isinstance(name, unicode):
        name = name.encode("gbk")
    else:
        name = name.decode("utf-8").encode("gbk")
    # 本地地址
    position = _position
    # 获取网页源代码
    html = requests.get(url).text

    title = re.findall("<h1 data-spm=\"1000983\">(.*?)</h1>", html, re.S)

    position = position + name + "/" + p

    data = re.findall(tmall_exp, html, re.S)
    data = json.loads(data[0])
    print(data['api']['descUrl'])
    detail_html = requests.get("http:" + data['api']["descUrl"]).text

    pic_url = re.findall(_regX, detail_html, re.S)

    if (len(pic_url) == 0):
        return

    # 如果文件夹不存在，则创建一个文件夹
    if not os.path.isdir(position):
        os.makedirs(position)
    else:
        shutil.rmtree(position)
        os.makedirs(position)

    i = 1
    for each in pic_url:
        print (each)
        pic = requests.get(each).content
        fp = open(position + "/" + str(i) + '.jpg', 'wb')
        fp.write(pic)
        fp.close()
        i += 1
    print("共爬取" + str(i) + "张图片")

if __name__ == "__main__":

    #print("开始爬取天猫商品图片")
    # 本地地址
    position = 'd:/img/'

    # 正则
    right = '<a href="#" style="background:url\((.*?)</span>'   #右侧小图对应
    left = '<a href="#"><img src=(.*?) /'    #左侧小图对应
    # detailRegx = 'src=(.*?) align=\"absmiddle\">'
    detailRegx = 'src=\"(https://img\S+?[jpgifn]+?)\"'

    url = sys.argv[1]
    print("url:" + url)
    #url = "https://detail.tmall.com/item.htm?spm=a1z10.5-b-s.w4011-15508258533.49.46eb549cFzdYWA&id=40615490643&rn=1c8c17d5a089752fcdd89a3416bc4ce0&abbucket=11,xiku"

    urls = url.split(",")
    name = urls[1].replace("\n","")
    if isinstance(name, unicode):
        name = name.encode("gbk")
    else:
        name = name.decode("utf-8").encode("gbk")


    dirPath = position + name
    if not os.path.isdir(dirPath):
        #参数 url, 储存位置, 爬取的正则
        getWindowInfo(object, url, position, left, "left")
        getWindowInfo(object, url, position, right, "right")
        getDetailInfo(object, url, position, detailRegx, "detail")

    # zip压缩
    startdir = dirPath  #要压缩的文件夹路径
    file_news = startdir +'.zip' # 压缩后文件夹的名字
    z = zipfile.ZipFile(file_news,'w',zipfile.ZIP_DEFLATED) #参数一：文件夹名
    for dirpath, dirnames, filenames in os.walk(startdir):
        fpath = dirpath.replace(startdir,'') #这一句很重要，不replace的话，就从根目录开始复制
        fpath = fpath and fpath + os.sep or ''#这句话理解我也点郁闷，实现当前文件夹以及包含的所有文件的压缩
        for filename in filenames:
            z.write(os.path.join(dirpath, filename),fpath+filename)
    z.close()

    #压缩完成后删除文件夹
    shutil.rmtree(dirPath) 