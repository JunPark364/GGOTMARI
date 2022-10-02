import { useEffect, useState } from "react";
import { useRouter } from "next/router";

import ArticleItem from "../../components/molecules/community/ArticleItem";
import SearchBar from "../../components/atoms/common/SearchBar";
import Header from "../../components/atoms/common/Header";

import { getArticleList, getPopularList } from "../../api/community";

import { FaPlus } from "react-icons/fa";

// export async function getStaticProps() {
//   var articles = [];
//   var popularArticles = [];

//   await getArticleList(
//     (res) => {
//       articles = res.data.articles;
//     },
//     (err) => {
//       console.log(err);
//     }
//   );

//   await getPopularList(
//     (res) => {
//       popularArticles = res.data.articles;
//     },
//     (err) => {
//       console.log(err);
//     }
//   );

//   return {
//     props: {
//       articles,
//       popularArticles,
//     },
//   };
// }

export async function getServerSideProps() {
  var articles;
  var popularArticles;

  await getArticleList(
    (res) => {
      articles = res.data.articles;
    },
    (err) => {
      console.log(err);
    },
  );

  await getPopularList(
    (res) => {
      popularArticles = res.data.articles;
    },
    (err) => {
      console.log(err);
    },
  );

  return { props: { articles, popularArticles } };
}

export default function Community({ articles, popularArticles }) {
  const router = useRouter();
  const tabs = ["전체", "팔로잉", "인기글"];
  const [tab, setTab] = useState("전체");
  const [currList, setCurrList] = useState([]);

  const articleList = articles;
  const followingList = articles.filter((article) => article.isFollow);
  const popularList = popularArticles;

  // const [articleList, setArticleList] = useState([]);
  // const [followingList, setFollowingList] = useState([]);
  // const [popularList, setPopularList] = useState([]);

  // useEffect(() => {
  //   getArticleList(
  //     (res) => {
  //       setArticleList(res.data.articles);
  //     },
  //     (err) => {
  //       console.log(err);
  //     },
  //   );

  //   getPopularList(
  //     (res) => {
  //       setPopularList(res.data.articles);
  //     },
  //     (err) => {
  //       console.log(err);
  //     },
  //   );
  // }, []);

  // useEffect(() => {
  //   setCurrList(articleList);
  //   setFollowingList(articleList.filter((article) => article.isFollow));
  // }, [articleList]);

  useEffect(() => {
    if (tab == "전체") {
      setCurrList(articleList);
    } else if (tab == "팔로잉") {
      setCurrList(followingList);
    } else if (tab == "인기글") {
      setCurrList(popularList);
    }
  }, [tab]);

  const handleAddClick = () => {
    router.push(
      {
        pathname: "/community/edit",
      },
      "/community",
    );
  };

  return (
    <div className="flex flex-col">
      <div className="flex flex-col items-center mb-6">
        <Header text={"우리들의 꽃 이야기"} />
        <SearchBar placeholder={"꽃 이야기 찾기"} />
      </div>
      <div className="flex flex-row border-b-2 border-font3">
        {tabs.map((tabName) => (
          <div
            className={`text-black text-xs font-sans px-5 py-3 ${
              tab === tabName ? "font-bold border-b-2 border-main" : ""
            }`}
            key={tabName}
            onClick={() => setTab(tabName)}
          >
            {tabName}
          </div>
        ))}
      </div>
      <div className="p-4 flex flex-col space-y-3">
        {currList.map((article) => (
          <ArticleItem article={article} key={article.articleId} />
        ))}
      </div>
      <div className="h-14"></div>
      <div className="fixed bottom-20 right-5 rounded-full w-12 aspect-square bg-sub1 shadow-lg flex justify-center items-center hover:scale-110 hover:bg-main duration-200">
        <FaPlus className="text-white text-2xl" onClick={handleAddClick} />
      </div>
    </div>
  );
}
