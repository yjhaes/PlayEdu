from pathlib import Path

path = Path('docs/backend-file-guide.md')
text = path.read_text(encoding='utf-8')
start = text.index('### playedu-points\n', text.index('## 五、'))
prefix, section = text[:start], text[start:]
rows = {}
for symbol in ('PointBalanceChange', 'PointBalanceChangeResult', 'PointBalanceException'):
    row = next(line for line in section.splitlines() if line.startswith(f'| [service/{symbol}.java]'))
    package = 'exception' if symbol == 'PointBalanceException' else 'types'
    rows[symbol] = row.replace(f'service/{symbol}.java', f'{package}/{symbol}.java')
    section = section.replace(row + '\n', '')
table_header = '| 文件 | 作用 | 方法、字段或 SQL 线索 |\n|---|---|---|\n'
section = section.replace('#### mapper\n', '#### exception\n\n' + table_header + rows['PointBalanceException'] + '\n\n#### mapper\n', 1)
section = section.replace('#### types\n\n' + table_header, '#### types\n\n' + table_header + rows['PointBalanceChange'] + '\n' + rows['PointBalanceChangeResult'] + '\n', 1)
resources = '#### 资源与配置\n\n' + table_header
for entity in ('PointCode', 'PointLedger', 'PointProduct', 'PointRedemption'):
    target = f'C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/main/resources/mapper/{entity}Mapper.xml'
    resources += f'| [src/main/resources/mapper/{entity}Mapper.xml]({target}) | 动态分页及计数查询、共享结果映射；固定注解查询复用此映射。 | namespace：xyz.playedu.points.mapper.{entity}Mapper；SQL：paginate、paginateCount |\n'
section = section.replace('#### crypto\n', resources + '\n#### crypto\n', 1)
test_row = '| [test/mapper/PointPaginationMapperIntegrationTest.java](C:/Users/86198/Desktop/play/playedu-api/playedu-points/src/test/java/xyz/playedu/points/mapper/PointPaginationMapperIntegrationTest.java) | 使用真实 MySQL 验证四个 Mapper 的筛选、列表/计数、分页排序、时间边界及枚举和字段映射。 | 测试：products、codes、ledgers、redemptions 的动态分页查询 |\n'
section = section.replace('#### 测试文件\n\n' + table_header, '#### 测试文件\n\n' + table_header + test_row, 1)
for entity, methods in {
    'PointCode': 'paginate、paginateCount、findByDigest、findAvailableForUpdate、markDelivered、countAvailableByProductId、countDeliveredByProductId、insertIgnore、deleteAvailableById、deleteAvailableByProductId',
    'PointLedger': 'paginate、paginateCount、findBySourceKey、findBySourceKeyForUpdate、insertIfAbsent',
    'PointProduct': 'paginate、paginateCount、selectByIdForUpdate',
    'PointRedemption': 'paginate、paginateCount、findByIdAndUserId、findByUserIdAndRequestKey、findByUserIdAndRequestKeyForUpdate',
}.items():
    row = next(line for line in section.splitlines() if line.startswith(f'| [mapper/{entity}Mapper.java]'))
    columns = row.split(' | ')
    columns[1] = '数据库访问接口；动态分页/计数 SQL 位于对应 XML，固定 SQL 保留注解并共享 XML 结果映射。'
    columns[2] = f'方法：{methods} |'
    section = section.replace(row, ' | '.join(columns))
path.write_bytes((prefix + section).replace('\n', '\r\n').encode('utf-8'))
