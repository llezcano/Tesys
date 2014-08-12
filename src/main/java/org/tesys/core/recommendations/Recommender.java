package org.tesys.core.recommendations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tesys.core.db.ElasticsearchDao;
import org.tesys.core.estructures.Developer;
import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;

public class Recommender {



  public List<Recommendation> recommendate(Metric m, String type) {
    
    ElasticsearchDao<Developer> dao = 
        new ElasticsearchDao<Developer>(Developer.class, 
            ElasticsearchDao.DEFAULT_RESOURCE_DEVELOPERS);

    List<Developer> developers = dao.readAll();


    List<Recommendation> recomendaciones = new ArrayList<Recommendation>();

    for (Developer d : developers) {
      Double fin = new Double(Double.NaN);
      for (Issue i : d.getIssues()) {
        if (i.getIssueType().equals(type)) {
          Double value = null;
          try {
            value = m.evaluate(i);
          } catch (NullPointerException e) {}
          if (value != null) {
            if (fin.equals(Double.NaN)) {
              fin = 0.0;
            }
            fin += value;
          }
        }
      }
      recomendaciones.add(new Recommendation(d.getName(), fin / d.getIssues().size()));
    }

    Collections.sort(recomendaciones);

    return recomendaciones;
  }

}
