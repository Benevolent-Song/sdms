# coding=utf-8

import json
import re


es_dict = {
    "info": {
        "category": "中华人民共和国广播电视和网络视听行业标准",
        "number": "",
        "titleCn": "",
        "titleEn": "",
        "issuedBy": "国家广播电视总局",
        "releaseDate": "",
        "implementDate": "",
        "domain": ""
    },
    "content": []
}


def read_txt(txt_path):
    # 逐行读取txt文件内容，并删除空行，返回一个列表
    lines = []
    with open(txt_path, 'r') as file_to_read:
        while True:
            line = file_to_read.readline()
            if not line:
                break
            if line == ' \n':
                line = line.strip(' \n')
            line = line.strip('\n')
            lines.append(line)
    while '' in lines:
        lines.remove('')
    return lines


global mark
global pre


def pdf2json(txt_input):
    flag = [0 for i in range(10)]
    # 全局变量，用于标记位置
    mark = [0 for i in range(10)]

    # 处理txt文本
    line_list = read_txt(txt_input)

    # 先处理正文部分前的内容
    for i in range(len(line_list)):

        if flag[1] == 0:
            if re.search('^G[A-Z]/T', line_list[i]) is not None:
                es_dict["info"]["number"] = line_list[i]
                mark[0] = i
                flag[1] = 1

        if re.search('\\d{4}\\s*-\\s*\\d{2}\\s*-\\s*\\d{2}\\s*\xe5\xae\x9e\xe6\x96\xbd', line_list[i]) is not None:
            es_dict["info"]["releaseDate"] = ''.join(re.findall('^\\d{4}\\s*-\\s*\\d{2}\\s*-\\s*\\d{'
                                                                '2}\\s*\xe5\x8f\x91\xe5\xb8\x83', line_list[i]))
            es_dict["info"]["implementDate"] = ''.join(re.findall('\\d{4}\\s*-\\s*\\d{2}\\s*-\\s*\\d{'
                                                                  '2}\\s*\xe5\xae\x9e\xe6\x96\xbd', line_list[i]))
            mark[1] = i
            for j in range(mark[0] + 1, mark[1]):
                if re.search('[\xb0\xa7-\xf7\xfe]', line_list[j]) is not None:
                    es_dict["info"]["titleCn"] += line_list[j]
                else:
                    es_dict["info"]["titleEn"] += line_list[j]

        if re.search('^前[\\s]+言\\s$', line_list[i]) is not None:
            mark[2] = i

        if re.search('^引[\\s]+言\\s$', line_list[i]) is not None:
            section = {"pid": "", "number": "", "titleCn": "", "chapter": "前 言", "text": "", "page": ""}
            mark[3] = i
            flag[2] = 1
            for j in range(mark[2] + 1, mark[3] - 2):
                section["text"] += line_list[j]
            es_dict["content"].append(section)

        if re.search('^G[A-Z]/?T?\\s[0-9]+\\.*[0-9]+—[1-2][0-9]{3}\\s', line_list[i]):
            if line_list[i + 1] == '1 ':
                mark[4] = i

        if re.search('^附[\\s]*录[\\s]*[A-Z]\\s$', line_list[i]) is not None:
            flag[3] = 1
            if flag[4] == 0:
                mark[5] = i
                # print(line_list[i])
                flag[4] = 1

    # 引言不存在则执行
    if flag[2] == 0:
        section = {"pid": "", "number": "", "titleCn": "", "chapter": "前 言", "text": "", "page": ""}
        for j in range(mark[2] + 1, mark[4]):
            section["text"] += line_list[j]
        es_dict["content"].append(section)

    # 引言存在则执行
    if flag[2] == 1:
        section = {"pid": "", "number": "", "titleCn": "", "chapter": "引 言", "text": "", "page": ""}
        for j in range(mark[3] + 1, mark[4]):
            section["text"] += line_list[j]
        es_dict["content"].append(section)

    # 正文部分,附录存在
    main_body_list = []
    if flag[3] == 1:
        for i in range(mark[4], mark[5]):
            main_body_list.append(line_list[i])
    # 附录不存在
    if flag[3] == 0:
        for i in range(mark[4], len(line_list)):
            main_body_list.append(line_list[i])
    # print(main_body_list)

    # 附录部分
    appendix_list = []
    for i in range(mark[5] - 3, len(line_list)):
        appendix_list.append(line_list[i])

    title_list = []
    for i in range(mark[2]):
        if re.search('^([1-9][0-9]?)\\s+([\\S])', line_list[i]):
            # print(line_list[i])
            title_list.append(re.search('^([1-9][0-9]?)\\s+([\\S])', line_list[i]).group(2))
    print(title_list)

    # 计算标题数量，将标题，页码位置记录在map中
    title_count = 0
    title_count2 = 0
    title_map = []
    tmp_map = []
    pre = 0

    for i in range(len(main_body_list)):

        if re.search(es_dict["info"]["number"], main_body_list[i]):
            tmp_map.append(i + 1)

        r1 = re.search(
            '^([1-9][0-9]?\\.?[1-9]?[0-9]*\\.?[1-9]?[0-9]*\\.?[1-9]?[0-9]*?\\.?[1-9]?[0-9]*)\\s(\\s|\\S)*$',
            main_body_list[i]) is not None
        r2 = re.search('^[2-9]\\d', main_body_list[i]) is None
        r3 = re.search('^[0-9]{3,}\\s+', main_body_list[i]) is None
        r4 = re.search('^[0-9]+\\s([0-9]|[—–>x-])', main_body_list[i]) is None
        r5 = re.search('^[1-9][0-9]?\\s+$', main_body_list[i]) is None
        r6 = re.search('^[1-9][0-9]?\\s{2,}\\S', main_body_list[i]) is None
        is_title = r1 and r2 and r3 and r4 and r5 and r6
        if is_title:

            if int(re.search('^(\\d+)', main_body_list[i]).group(1)) >= pre:
                now = int(re.search('^(\\d+)', main_body_list[i]).group(1))
                if now == pre:
                    if re.search('^[1-9][0-9]?\\.', main_body_list[i]):
                        print(main_body_list[i])
                        title_count += 1
                        title_map.append(i)
                if (now - pre) == 1:
                    if title_count2 < len(title_list):
                        if re.search('\\.', main_body_list[i]) is None:
                            # print(main_body_list[i])
                            if main_body_list[i].split(' ')[1][0] == title_list[title_count2]:
                                title_count2 += 1
                                pre = now
                                print(main_body_list[i])
                                title_count += 1
                                title_map.append(i)

    print("=====section数量=====")
    print(title_count)


    page_map = []
    for i in range(len(tmp_map)):
        if re.search('\\d', main_body_list[tmp_map[i]]):
            page_map.append(tmp_map[i])
    print(page_map)

    for i in range(len(title_map)):
        section = {"pid": "", "number": "", "titleCn": "", "chapter": "", "text": "", "page": ""}
        if flag[5] == 0:
            title_map.append(len(main_body_list))
            flag[5] = 1
        section["text"] = ''.join(main_body_list[title_map[i]:title_map[i + 1]])
        section["chapter"] = ''.join(main_body_list[title_map[i]])
        for j in range(len(page_map)):
            if flag[6] == 0:
                page_map.append(len(main_body_list))
                flag[6] = 1
            if page_map[j] < title_map[i] < page_map[j + 1]:
                section["page"] = ''.join(main_body_list[page_map[j]])
        es_dict["content"].append(section)

    # 计算附录数量，将位置记录在map中
    appendix_map = []
    append_count = 0
    for i in range(len(appendix_list)):
        if re.search('^附\\s*录\\s*[A-Z]\\s$', appendix_list[i]) is not None:
            print(appendix_list[i])
            appendix_map.append(i)
            append_count += 1
    print("======附录数量======")
    print(append_count)

    for i in range(len(appendix_map)):
        section = {"pid": "", "number": "", "titleCn": "", "chapter": "", "text": "", "page": ""}
        if flag[8] == 0:
            appendix_map.append(len(appendix_list))
            flag[8] = 1
        section["text"] = ''.join(appendix_list[appendix_map[i]:appendix_map[i + 1]])
        section["chapter"] = ''.join(appendix_list[appendix_map[i]])
        es_dict["content"].append(section)

    for i in range(len(es_dict["content"])):
        es_dict["content"][i]["number"] = es_dict["info"]["number"]
        es_dict["content"][i]["titleCn"] = es_dict["info"]["titleCn"]
    # print(es_dict)

    json_obj = json.dumps(es_dict, indent=4)

    return json_obj


