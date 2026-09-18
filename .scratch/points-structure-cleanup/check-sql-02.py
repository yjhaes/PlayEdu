from pathlib import Path
import re
import subprocess
import xml.etree.ElementTree as ET

def signature(element):
    return (' '.join((element.text or '').split()), tuple((child.tag, tuple(sorted(child.attrib.items())), signature(child), ' '.join((child.tail or '').split())) for child in element))

for entity in ('PointProduct', 'PointCode', 'PointLedger', 'PointRedemption'):
    java = Path('playedu-api/playedu-points/src/main/java/xyz/playedu/points/mapper') / (entity + 'Mapper.java')
    before = subprocess.check_output(['git', 'show', 'HEAD:' + java.as_posix()]).decode()
    after = java.read_text(encoding='utf-8')
    old_queries = re.findall(r'<script>(.*?)</script>', before, re.S)
    root = ET.parse(Path('playedu-api/playedu-points/src/main/resources/mapper') / (entity + 'Mapper.xml')).getroot()
    assert root.attrib['namespace'] == 'xyz.playedu.points.mapper.' + entity + 'Mapper'
    for old, method in zip(old_queries, ('paginate', 'paginateCount')):
        assert signature(ET.fromstring('<script>' + old + '</script>')) == signature(root.find(f"select[@id='{method}']")), (entity, method)
    fixed = lambda text: [query for query in re.findall(r'@(Select|Insert|Update|Delete)\((.*?)\)\s*(?=@|(?:\w|List<))', text, re.S) if '<script>' not in query[1]]
    assert fixed(before) == fixed(after), entity
    assert '<script>' not in after
    print(entity + ': dynamic SQL identical; fixed SQL unchanged')
