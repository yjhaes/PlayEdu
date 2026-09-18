from pathlib import Path
import re
import textwrap

base = Path('playedu-api/playedu-points/src/main')
for entity in ('PointProduct', 'PointCode', 'PointLedger', 'PointRedemption'):
    path = base / 'java/xyz/playedu/points/mapper' / (entity + 'Mapper.java')
    original = path.read_text(encoding='utf-8')
    selects = []
    pattern = r'    @Select\(\s*"""\s*<script>\n(.*?)\s*</script>\s*"""\)\n(?:    @ResultMap\("[^"]+"\)\n)?(?=    (?:List<[^>]+>|long) (\w+)\()'

    def remove_select(match):
        selects.append((match.group(2), textwrap.dedent(match.group(1)).rstrip()))
        return ''

    content, count = re.subn(pattern, remove_select, original, flags=re.S)
    assert count == 2, (entity, count)
    result = re.search(r'    @Results\(\s*id = "([^"]+)",\s*value = \{(.*?)\}\)\n', content, re.S)
    assert result
    map_id = result.group(1)
    entries = []
    for column, prop, java_type in re.findall(r'@Result\(column = "([^"]+)", property = "([^"]+)"(?:, javaType = (\w+)\.class)?\)', result.group(2)):
        type_attr = f' javaType="xyz.playedu.points.domain.{java_type}"' if java_type else ''
        entries.append(f'        <result column="{column}" property="{prop}"{type_attr}/>')
    assert entries
    content = content[:result.start()] + f'    @ResultMap("{map_id}")\n' + content[result.end():]
    content = content.replace('import org.apache.ibatis.annotations.Result;\n', '').replace('import org.apache.ibatis.annotations.Results;\n', '')
    path.write_bytes(content.replace('\n', '\r\n').encode('utf-8'))
    xml = ['<?xml version="1.0" encoding="UTF-8"?>', '<!DOCTYPE mapper', '        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"', '        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">', f'<mapper namespace="xyz.playedu.points.mapper.{entity}Mapper">', '', f'    <resultMap id="{map_id}" type="xyz.playedu.points.domain.{entity}">', *entries, '    </resultMap>']
    for name, sql in selects:
        attr = f'resultMap="{map_id}"' if name == 'paginate' else 'resultType="long"'
        xml += ['', f'    <select id="{name}" {attr}>', textwrap.indent(sql, '        '), '    </select>']
    xml += ['</mapper>', '']
    dest = base / 'resources/mapper' / (entity + 'Mapper.xml')
    dest.parent.mkdir(parents=True, exist_ok=True)
    dest.write_bytes('\r\n'.join(xml).replace('\r\n', '\n').replace('\n', '\r\n').encode('utf-8'))
