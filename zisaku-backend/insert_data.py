import sqlite3
import random
import bcrypt

# -------------------------------------------------------------
# 【準備】実在するレストランブランド、住所、そしてジャンル別画像プールの定義
# -------------------------------------------------------------

# 各ジャンルに完全に連動したUnsplashの高品質・高解像度画像プール（計50枚以上）
genre_images = {
    'ラーメン': [
        'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1607301408220-e8d3df4d7c41?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1591814468924-cafb06223adc?w=600&auto=format&fit=crop&q=80'
    ],
    '中華そば': [
        'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1557872943-16a5ac26437e?w=600&auto=format&fit=crop&q=80'
    ],
    '中華料理': [
        'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=600&auto=format&fit=crop&q=80'
    ],
    'イタリアン': [
        'https://images.unsplash.com/photo-1546549032-9571cd6b27df?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=600&auto=format&fit=crop&q=80'
    ],
    'フレンチ': [
        'https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1600565193348-f74bd3c7ccdf?w=600&auto=format&fit=crop&q=80'
    ],
    'バル・ビストロ': [
        'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1470337458703-46ad1756a187?w=600&auto=format&fit=crop&q=80'
    ],
    '居酒屋': [
        'https://images.unsplash.com/photo-1572116553112-75679234da54?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1590577976322-3d2d6aa900e5?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1625250479600-bf7a05d5d64e?w=600&auto=format&fit=crop&q=80'
    ],
    '居酒屋・ダイニングバー': [
        'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1572116553112-75679234da54?w=600&auto=format&fit=crop&q=80'
    ],
    'カフェ': [
        'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1469957761103-559364b285ea?w=600&auto=format&fit=crop&q=80'
    ],
    'スイーツ・パン': [
        'https://images.unsplash.com/photo-1551024601-bec78aea704b?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600&auto=format&fit=crop&q=80'
    ],
    '和食': [
        'https://images.unsplash.com/photo-1583623025817-d180a2221d0a?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?w=600&auto=format&fit=crop&q=80'
    ],
    '定食・食堂': [
        'https://images.unsplash.com/photo-1583623025817-d180a2221d0a?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1547592180-85f173990554?w=600&auto=format&fit=crop&q=80'
    ],
    '寿司': [
        'https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1611143669354-fbc8265003b5?w=600&auto=format&fit=crop&q=80'
    ],
    '焼肉': [
        'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=600&auto=format&fit=crop&q=80'
    ],
    '焼肉・ホルモン': [
        'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600&auto=format&fit=crop&q=80'
    ],
    '焼き鳥': [
        'https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1532636875304-0c8fe119aa9e?w=600&auto=format&fit=crop&q=80'
    ],
    'ステーキ・ハンバーグ': [
        'https://images.unsplash.com/photo-1600891964599-f61ba0e24092?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&auto=format&fit=crop&q=80'
    ],
    'ハンバーガー': [
        'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1550547660-d9450f859349?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=600&auto=format&fit=crop&q=80'
    ],
    'うどん・そば': [
        'https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1606755456206-b25206cde27e?w=600&auto=format&fit=crop&q=80'
    ],
    'カレー': [
        'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1626132647523-66f5bf380027?w=600&auto=format&fit=crop&q=80'
    ],
    '韓国料理': [
        'https://images.unsplash.com/photo-1553163147-622ab578d84a?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1632161958220-449e75a8947f?w=600&auto=format&fit=crop&q=80'
    ],
    'タイ料理': [
        'https://images.unsplash.com/photo-1559314809-0d155014e29e?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=600&auto=format&fit=crop&q=80'
    ],
    'エスニック': [
        'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop&q=80'
    ],
    'お好み焼き・たこ焼き': [
        'https://images.unsplash.com/photo-1601000922839-a9a7a92fb47f?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&auto=format&fit=crop&q=80'
    ],
    '鍋料理': [
        'https://images.unsplash.com/photo-1547592180-85f173990554?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1590577976322-3d2d6aa900e5?w=600&auto=format&fit=crop&q=80'
    ],
    '郷土料理': [
        'https://images.unsplash.com/photo-1583623025817-d180a2221d0a?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?w=600&auto=format&fit=crop&q=80'
    ],
    '海鮮料理': [
        'https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=600&auto=format&fit=crop&q=80'
    ],
    '洋食': [
        'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600&auto=format&fit=crop&q=80'
    ],
    '創作料理': [
        'https://images.unsplash.com/photo-1604544463336-23db0ef1d4b5?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&auto=format&fit=crop&q=80'
    ],
    '天ぷら・串揚げ': [
        'https://images.unsplash.com/photo-1583623025817-d180a2221d0a?w=600&auto=format&fit=crop&q=80',
        'https://images.unsplash.com/photo-1607330289024-1535c6b4e1c1?w=600&auto=format&fit=crop&q=80'
    ]
}

# 万が一のフォールバック用外観画像
default_images = [
    'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=600&auto=format&fit=crop&q=80'
]

real_brands = {
    'ラーメン': ['一蘭', '天下一品', '一風堂', '蒙古タンメン中本', 'らあめん花月嵐', '幸楽苑', '日高屋', '麺屋武蔵', '青葉', 'つじ田'],
    'イタリアン': ['サイゼリヤ', 'カプリチョーザ', '洋麺屋五右衛門', 'ジョリーパスタ', 'サルヴァトーレ クオモ', 'カポカヴァーロ'],
    '居酒屋': ['鳥貴族', '串カツ田中', '世界の山ちゃん', '庄や', '魚民', '笑笑', '白木屋', 'はなの舞', '天狗'],
    'カフェ': ['スターバックスコーヒー', 'ドトールコーヒー', 'タリーズコーヒー', 'コメダ珈琲店', 'サンマルクカフェ', '星乃珈琲店', 'プロント'],
    '和食': ['大戸屋', 'やよい軒', '木曽路', 'とんかつ新宿さぼてん', '藍屋', '夢庵', '梅の花'],
    '焼肉': ['牛角', '焼肉きんぐ', '叙々苑', '安楽亭', '牛繁', '焼肉トラジ', '情熱ホルモン'],
    '寿司': ['スシロー', 'くら寿司', 'はま寿司', 'かっぱ寿司', 'すしざんまい', '回し寿司 活美登利'],
    '焼き鳥': ['鳥貴族', '鳥メロ', 'とり鉄', '串鳥', '本格焼鳥 おおとり'],
    'フレンチ': ['俺のフレンチ', 'ブラッスリー・ポール・ボキューズ', 'ひらまつ', 'ビストロ・ド・リヨン'],
    '中華料理': ['餃子の王将', '大阪王将', 'バーミヤン', '紅虎餃子房', '東天紅', '銀座アスター'],
    'カレー': ['CoCo壱番屋', 'ゴーゴーカレー', 'カレーハウス サンマルコ', 'もうやんカレー', 'マジックスパイス'],
    'ハンバーガー': ['マクドナルド', 'モスバーガー', 'ケンタッキーフライドチキン', 'バーガーキング', 'ロッテリア', 'フレッシュネスバーガー'],
    'うどん・そば': ['丸亀製麺', 'はなまるうどん', '名代 富士そば', 'ゆで太郎', 'そじ坊', '山田うどん'],
    '韓国料理': ['妻家房', '韓美膳', 'チェゴヤ', 'ビビゴ', '吾照里'],
    'タイ料理': ['マンゴツリーカフェ', 'ティーヌン', 'コカレストラン', 'バンコクキッチン'],
    'ステーキ・ハンバーグ': ['いきなり!ステーキ', 'ブロンコビリー', 'ビッグボーイ', 'ハングリータイガー', 'カウボーイ家族'],
    'バル・ビストロ': ['PRONTO', 'ヱビスバー', 'HUB', 'ヴィノシティ', 'バグース', 'ディプント'],
    'スイーツ・パン': ['ミスタードーナツ', 'サーティワンアイスクリーム', 'クリスピー・クリーム・ドーナツ', 'シャトレーゼ', '不二家'],
    '居酒屋・ダイニングバー': ['プロントイルバール', 'HUB', '鳥貴族', 'キリストンカフェ', 'バグース'],
    '創作料理': ['KICHIRI', '美食米門', '響', 'ほの字', '権八'],
    '天ぷら・串揚げ': ['天丼てんや', '串家物語', '串かつ だるま', '天ぷら新宿つな八'],
    'お好み焼き・たこ焼き': ['ぼてぢゅう', '道とん堀', '築地銀だこハイボール酒場', '鶴橋風月'],
    '鍋料理': ['しゃぶしゃぶ温野菜', '赤から', '木曽路', '博多もつ鍋 おおやま'],
    '郷土料理': ['塚田農場', '四十八漁場', '沖縄地料理 波照間', 'ねぎし'],
    '海鮮料理': ['磯丸水産', 'さくら水産', '庄や', '源ちゃん', '魚盛'],
    '中華そば': ['日高屋', '幸楽苑', '喜多方ラーメン坂内', 'ちりめん亭', '青葉'],
    '洋食': ['ガスト', 'デニーズ', 'ロイヤルホスト', 'ジョナサン', 'ココス', 'つばめグリル'],
    'エスニック': ['モンスーンカフェ', 'ゼストキャンティーナ', 'トウキョウサザンアイランド'],
    '焼肉・ホルモン': ['牛角', '情熱ホルモン', 'ホルモン青木', '山田屋', '亀戸ホルモン'],
    '定食・食堂': ['まいどおおきに食堂', '大戸屋ごはん処', 'やよい軒', 'しんぱち食堂', '大衆食堂 半田屋']
}

branch_suffixes = ['店', '駅前店', '中央通り店', '北口店', '南口店', '西口店', '東口店', 'アトレ店', '前店']

rest_genres = list(real_brands.keys())

rest_areas = [
    '渋谷', '新宿', '池袋', '銀座', '六本木', '原宿', '表参道', '青山', '代官山', '恵比寿',
    '中目黒', '目黒', '品川', '大崎', '五反田', '新橋', '汐留', '有楽町', '日比谷', '大手町',
    '東京', '日本橋', '京橋', '八重洲', '神田', '秋覇原', '御茶ノ水', '上野', '浅草', '押上',
    '自由が丘', '二子玉川', '三軒茶屋', '下北沢', '高円寺', '阿佐ヶ谷', '荻窪', '吉祥寺', '中野', '西荻窪',
    '横浜', 'みなとみらい', '元町中華街', '川崎', '武蔵小杉', '大宮', '浦和', '船橋', '柏', '浦安'
]

area_to_towns = {
    '渋谷': ['道玄坂', '宇田川町', '神宮前', '円山町', '恵比寿西', '広尾', '渋谷', '桜丘町'],
    '新宿': ['歌舞伎町', '西新宿', '新宿', '高田馬場', '神楽坂', '百人町', '北新宿', '三栄町'],
    '池袋': ['東池袋', '西池袋', '南池袋', '上池袋', '池袋', '池袋本町'],
    '銀座': ['銀座', '築地', '八丁堀', '入船', '新富'],
    '六本木': ['六本木', '麻布十番', '西麻布', '東麻布', '南麻布'],
    '原宿': ['神宮前', '千駄ヶ谷', '代々木'],
    '表参道': ['神宮前', '南青山', '北青山'],
    '青山': ['南青山', '北青山', '西麻布'],
    '代官山': ['代官山町', '恵比寿西', '猿楽町', '鉢山町'],
    '恵比寿': ['恵比寿', '恵比寿西', '恵比寿南', '広尾'],
    '中目黒': ['上目黒', '中目黒', '青葉台', '東山'],
    '目黒': ['目黒', '下目黒', '三田', '祐天寺'],
    '品川': ['高輪', '港南', '北品川', '東品川'],
    '大崎': ['大崎', '西品川', '大井'],
    '五反田': ['西五反田', '東五反田', '大崎'],
    '新橋': ['新橋', '西新橋', '東新橋', '海岸'],
    '汐留': ['東新橋', '海岸', '新橋'],
    '有楽町': ['グライド有楽町', '丸の内', '日比谷公園'],
    '日比谷': ['有楽町', '内幸町', '丸の内'],
    '大手町': ['大手町', '丸の内', '神田錦町'],
    '東京': ['丸の内', '大手町', '八重洲', '日本橋'],
    '日本橋': ['日本橋', '日本橋室町', '日本橋本町', '日本橋兜町'],
    '京橋': ['京橋', '宝町', '八重洲'],
    '八重洲': ['八重洲', '京橋', '日本橋'],
    '神田': ['内神田', '外神田', '鍛冶町', '神田須田町'],
    '秋覇原': ['外神田', '神田佐久間町', '秋葉原'],
    '御茶ノ水': ['神田駿河台', '湯島', '本郷'],
    '上野': ['上野', '東上野', '下谷', '根岸'],
    '浅草': ['浅草', '西浅草', '雷門', '花川戸'],
    '押上': ['押上', '業平', '向島'],
    '自由が丘': ['自由が丘', '緑が丘', '奥沢'],
    '二子玉川': ['玉川', '瀬田', '上野毛'],
    '三軒茶屋': ['三軒茶屋', '太子堂', '若林'],
    '下北沢': ['北沢', '代沢', '代田'],
    '高円寺': ['高円寺北', '高円寺南', '梅里'],
    '阿佐ヶ谷': ['阿佐谷北', '阿佐谷南', '天沼'],
    '荻窪': ['荻窪', '上荻', '南荻窪'],
    '吉祥寺': ['吉祥寺本町', '吉祥寺東町', '吉祥寺南町', '御殿山'],
    '中野': ['中野', '東中野', '新井', '野方'],
    '西荻窪': ['西荻北', '西荻南', '松庵'],
    '横浜': ['西区高島', '西区みなとみらい', '神奈川区鶴屋町'],
    'みなとみらい': ['西区みなとみらい', '中区桜木町'],
    '元町中華街': ['中区山下町', '中区元町', '中区新山下'],
    '川崎': ['川崎区駅前本町', '川崎区砂子', '幸区堀川町'],
    '武蔵小杉': ['中原区小杉町', '中原区新丸子町'],
    '大宮': ['大宮区大門町', '大宮区宮町', '大宮区仲町'],
    '浦和': ['浦和区高砂', '浦和区仲町', '浦和区東高砂'],
    '船橋': ['本町', '湊町', '宮本'],
    '柏': ['柏', '末広町', '旭町'],
    '浦安': ['美浜', '入船', '北栄']
}

def get_real_address(area, i):
    if area in ['横浜', 'みなとみらい', '元町中華街']:
        pref_city = "神奈川県横浜市"
    elif area in ['川崎', '武蔵小杉']:
        pref_city = "神奈川県川崎市"
    elif area in ['大宮', '浦和']:
        pref_city = "埼玉県さいたま市"
    elif area == '船橋':
        pref_city = "千葉県船橋市"
    elif area == '柏':
        pref_city = "千葉県柏市"
    elif area == '浦安':
        pref_city = "千葉県浦安市"
    else:
        sub_districts = {
            '渋谷': '渋谷区', '新宿': '新宿区', '池袋': '豊島区', '銀座': '中央区', '六本木': '港区',
            '原宿': '渋谷区', '表参道': '港区', '青山': '港区', '代官山': '渋谷区', '恵比寿': '渋谷区',
            '中目黒': '目黒区', '目黒': '目黒区', '品川': '港区', '大崎': '品川区', '五反田': '品川区',
            '新橋': '港区', '汐留': '港区', '有楽町': '千代田区', '日比谷': '千代田区', '大手町': '千代田区',
            '東京': '千代田区', '日本橋': '中央区', '京橋': '中央区', '八重洲': '中央区', '神田': '千代田区',
            '秋覇原': '千代田区', '御茶ノ水': '千代田区', '上野': '台東区', '浅草': '台東区', '押上': '墨田区',
            '自由が丘': '目黒区', '二子玉川': '世田谷区', '三軒茶屋': '世田谷区', '下北沢': '世田谷区',
            '高円寺': '杉並区', '阿佐ヶ谷': '杉並区', '荻窪': '杉並区', '吉祥寺': '武蔵野市', '中野': '中野区',
            '西荻窪': '杉並区'
        }
        pref_city = f"東京都{sub_districts.get(area, '新宿区')}"
    
    towns = area_to_towns.get(area, ['中央'])
    town = towns[i % len(towns)]
    
    chome = (i % 4) + 1
    ban = (i % 12) + 1
    go = (i % 15) + 1
    
    if "区" in town and "市" in pref_city:
        return f"{pref_city.split('市')[0]}市{town}{chome}-{ban}-{go}"
    if "市" in pref_city and "区" not in pref_city:
        return f"{pref_city}{town}{chome}-{ban}-{go}"
        
    return f"{pref_city}{town}{chome}丁目{ban}-{go}"

rest_prices = ['¥1,000〜¥2,000', '¥2,000〜¥4,000', '¥4,000〜¥6,000', '¥6,000〜']

hour_patterns = [
    ("11:30", "22:00"), ("11:00", "23:00"), ("17:00", "05:00"), ("10:00", "20:00"),
    ("00:00", "23:59"), ("11:30", "22:00"), ("12:00", "21:00"), ("06:00", "15:00"),
    ("11:00", "22:00"), ("18:00", "02:00"), ("11:30", "15:00"), ("11:30", "22:00")
]

day_off_patterns = [
    '年中無休', '月曜日', '火曜日', '水曜日', '木曜日', '日曜日・祝日',
    '不定休 (商業施設に準ずる)', '第2・第4水曜日', '毎週月曜・第一火曜'
]

# -------------------------------------------------------------
# データベース接続とリセット
# -------------------------------------------------------------
DB_PATH = r"C:/src/zisaku_app/zisaku-backend/database.db"
conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

print("🧹 既存のデータを削除中...")
cursor.execute("DELETE FROM t_user_restaurant;")
cursor.execute("DELETE FROM m_user;")
cursor.execute("DELETE FROM m_restaurant;")
conn.commit()
print("✨ リセット完了。新しいデータを挿入します。")

# -------------------------------------------------------------
# 1. m_restaurant（レストランデータ 7,000件）の生成と挿入
# -------------------------------------------------------------
restaurants_list = []
print("🏪 実在風レストラン・住所・ジャンル連動写真を生成中（20,000件）...")

for i in range(1, 20000):
    genre = rest_genres[(i + (i // 10)) % len(rest_genres)]
    area = rest_areas[(i + (i // 5)) % len(rest_areas)]
    price = rest_prices[(i + (i // 3)) % len(rest_prices)]
    
    # 店名の決定
    brands = real_brands.get(genre, ['美味しいお店'])
    brand_name = brands[i % len(brands)]
    suffix_str = branch_suffixes[(i // 3) % len(branch_suffixes)]
    name = f"{brand_name} {area}{suffix_str}"
    
    # 住所の決定
    address = get_real_address(area, i)
    
    # 🌟 写真の決定（ジャンルごとに複数枚あるプールから均等に配分）
    img_pool = genre_images.get(genre, default_images)
    image = img_pool[i % len(img_pool)]
    
    # 評価ロジック
    rating = 3.5 + random.random() * 1.4
    if i % 23 == 0: rating = 4.5 + random.random() * 0.5
    if i % 47 == 0: rating = 1.0 + random.random() * 1.5
    final_rating = round(min(5.0, max(1.0, rating)), 1)
    
    open_hour, close_hour = hour_patterns[(i * 17) % len(hour_patterns)]
    closed_days = day_off_patterns[(i * 29) % len(day_off_patterns)]
    phone = f"03-{1000 + (i % 8999)}-{random.randint(1000, 9999)}"
    
    num_str = f"{i:04d}"
    url = f"https://example.com/rest{num_str}" if (i % 10) < 6 else ""
    
    restaurants_list.append((
        None, name, image, final_rating, genre, price, area, 
        open_hour, close_hour, address, phone, url, closed_days
    ))

cursor.executemany("""
    INSERT INTO m_restaurant VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
""", restaurants_list)

# -------------------------------------------------------------
# 2. m_user & t_user_restaurant（ユーザーデータ 900件）の生成と挿入
# -------------------------------------------------------------
first_names = ['小川', '吉田', '山田', '佐々木', '山口', '松本', '井上', '木村', '林', '斎藤']
last_names = ['太郎', '花子', '一郎', '良子', '健太', '美咲', '翼', 'さくら', '翔', '葵']

print("🔑 ユーザーのパスワードをハッシュ化してインサート中...")

for i in range(1, 20):
    u_id = i
    raw_pwd = f"Pass{i:04d}!"
    hashed_pwd = bcrypt.hashpw(raw_pwd.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
    
    name = f"{first_names[i % len(first_names)]} {last_names[(i * 3) % len(last_names)]}"
    email = f"random{i}@example.com"
    phone = f"090{random.randint(10000000, 99999999)}"
    
    cursor.execute("""
        INSERT INTO m_user (user_id, user_password, user_name, user_email, user_phone) 
        VALUES (?, ?, ?, ?, ?);
    """, (u_id, hashed_pwd, name, email, phone))
    
    target_count = random.randint(0, 10)
    fav_ids = random.sample(range(1, 7001), target_count)
    
    for rest_id in fav_ids:
        cursor.execute("""
            INSERT INTO t_user_restaurant (user_id, restaurant_id, is_favorite) 
            VALUES (?, ?, 1);
        """, (u_id, rest_id))

conn.commit()
conn.close()

print("✨ 成功: ジャンル最適化された写真・店名・住所を含む全7,000件のデータをインサートしました！")