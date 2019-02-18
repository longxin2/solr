package test;

import it.lxx.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinxinLong on 2019/2/18
 * 备注：
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ="classpath:applicationContext-solr.xml")

public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    //增加和修改
    @Test
    public void testAdd() {
        TbItem item = new TbItem();

        item.setId(1l);
        item.setBrand("Apple");
        item.setCategory("手机");
        item.setGoodsId(1l);
        item.setSeller("Apple官方旗舰店");
        item.setTitle("iPhone XS Plus");
        item.setPrice(new BigDecimal(10998.00));

        //封装
        solrTemplate.saveBean(item);

        //提交
        solrTemplate.commit();

    }



    //按主键查询
    @Test
    public void testFindOne() {
        TbItem item = solrTemplate.getById(1, TbItem.class);
        System.out.println(item.getTitle());

    }


    //按主键删除
    @Test
    public void testDeleOne() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();//切记提交
        System.out.println("删除成功");
    }



    //批量添加
    @Test
    public void testAddAll() {
        //创建list集合存放
        List<TbItem> list = new ArrayList();
        //循环插入
        for (int i=0;i<100;i++) {
            TbItem item = new TbItem();

            item.setId(i+1l);
            item.setBrand("Apple");
            item.setCategory("手机");
            item.setGoodsId(i+1l);
            item.setSeller("Apple官方旗舰店");
            item.setTitle("iPhone "+(i+1)+" Plus");
            item.setPrice(new BigDecimal(10998.00+(i*10)));
            //添加到list集合
            list.add(item);
        }
        //存入solr
        solrTemplate.saveBeans(list);
        //提交
        solrTemplate.commit();
    }



    //分页查询
    @Test
    public void testPageQuery() {

        //设置查询条件
        Query query = new SimpleQuery("item_brand:Apple");
        //设置开始索引(默认为0)
        query.setOffset(0);
        //设置每页条数(默认为10条)
        query.setRows(20);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总条数:"+page.getTotalElements());
        System.out.println("总页数:"+page.getTotalPages());

        List<TbItem> list = page.getContent();
        showList(list);
    }


    //显示数据
    public void showList(List<TbItem> list) {
        //遍历
        for (TbItem item : list) {
            System.out.println(item.getTitle()+"    "+item.getPrice());
        }
    }



    //按条件查询
    @Test
    public void testPageQueryMutil() {

        //设置查询条件
        Query query = new SimpleQuery("item_brand:Apple");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria = criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        //设置开始索引(默认为0)
//        query.setOffset(0);
        //设置每页条数(默认为10条)
//        query.setRows(20);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总条数:"+page.getTotalElements());
        System.out.println("总页数:"+page.getTotalPages());

        List<TbItem> list = page.getContent();
        showList(list);
    }


    //批量删除
    @Test
    public void testDeleAll() {
        Query query = new SimpleQuery("item_title:Apple");
        //删除
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();
        System.out.println("删除成功");
    }

}
