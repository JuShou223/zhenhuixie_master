# Defect Log

Claude 造成的 bug 复盘记录，按时间倒序追加。

---

## [2026-05-08] 评论回复因缺少 commentCount 字段不显示

**类型**: 接口契约不符
**文件**:
- `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/ZhwCommentMapper.xml`
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/ZhwComment.java`
- `uni-app-starter/src/pages/story/comments.vue`

### 现象
用户发布回复后，评论列表页面看不到该回复。移动端 `/zhw/comment/list` 返回 `total: 2`，只有 2 条根评论，新回复（`parentId: 100`）完全不出现。管理后台接口能正常查到该条回复（`commentId: 108`）。

### 根因
写前端 `fetchList()` 时用了 `if (c.commentCount > 0)` 来决定是否加载回复，隐含"后端会返回 `commentCount` 字段"的假设。但写后端 `selectRootComments` 时没有加对应的回复数子查询，字段从来没有出现在响应里。

**认知层面**：前后端分批写，前端写判断逻辑时"脑补"了一个还不存在的字段，把"计划要加"当成"已经存在"。字段名合理、逻辑正确，但没有及时核查后端是否真的返回了它——这是一种典型的"假设即事实"思维陷阱。

### 修法
1. `ZhwComment.java` 新增 `commentCount` 字段及 getter/setter
2. `ZhwCommentMapper.xml` resultMap 加 `<result property="commentCount" column="comment_count"/>`
3. `selectRootComments` 加子查询：`(SELECT COUNT(1) FROM zhw_comment r WHERE r.parent_id = q.comment_id AND r.del_flag = '0') AS comment_count`

### 预防规则
写前端代码时用到 `obj.someField`（尤其是判断条件如 `> 0`、`=== true`），立刻反问：**后端响应里有没有这个字段？** 打开对应 mapper XML 或接口定义，grep 字段名，确认存在后再继续。不能靠"感觉应该有"。

### Memory 更新
新增 → `feedback_api_contract.md`（接口契约：前端使用字段前必须确认后端已返回）

---

## [2026-05-07] storyCount 与故事列表数量不一致

**类型**: 数据一致性 + 遗漏级联（双重）
**文件**:
- `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/ZhwTopicMapper.xml`
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/ZhwStoryServiceImpl.java`

### 现象
`GET /zhw/topic/1` 返回 `storyCount: 3`，但 `GET /zhw/topic/1/stories` 返回 `total: 0, rows: []`。
话题详情显示有 3 个故事，广场列表却空白。

### 根因
两个独立缺陷叠加，均源于**分批写代码时丢失了全局数据视图**：

1. **口径不一致**：写 `selectTopicById` 时用了最简单的子查询直接数 `zhw_story_topic` 行数，没有过滤 story 的 `del_flag`/`status`；写 `selectTopicStories` 时加了 `WHERE s.del_flag='0' AND s.status='0'`。两段 SQL 各自"局部正确"，但没有人在写第二段时回头检查第一段的口径是否一致。

2. **遗漏级联**：写 `adminDeleteStory` 时只更新了 `zhw_story.del_flag='2'`，没有追问"zhw_story 的主键 story_id 还被哪些表引用"。`zhw_story_topic` 是多对多中间表，无数据库外键约束，孤儿记录悄悄积累。

### 修法
1. `selectTopicById` 和 `selectTopicList` 的 storyCount 子查询，加 JOIN zhw_story 过滤 `del_flag='0' AND status='0'`，使统计口径与列表查询一致
2. `adminDeleteStory` 加 `@Transactional`，在软删 story 后同步调用 `topicMapper.deleteStoryTopicByStoryId(storyId)` 清理关联记录
3. `ZhwTopicMapper` 新增 `deleteStoryTopicByStoryId(Long storyId)` 方法及对应 XML
4. 数据库执行清理 SQL，删除 3 条孤儿记录（story_id 1、102、104 均已删除或不存在）

### 预防规则
1. 写完任何 delete/软删逻辑后，grep 主键字段名，检查所有中间表是否需要同步清理
2. 写完 count 子查询后，找到对应列表 SQL，逐条比对 WHERE 条件是否一致

### Memory 更新
已有覆盖 → `feedback_cascade_and_consistency.md`（本次 bug 的根因和预防规则均已在该文件中记录，无需新增）
